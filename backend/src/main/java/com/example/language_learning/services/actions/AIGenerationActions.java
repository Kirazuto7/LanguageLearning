package com.example.language_learning.services.actions;

import com.example.language_learning.config.AIConfig;
import com.example.language_learning.enums.PromptType;
import com.example.language_learning.services.contexts.AIGenerationContext;
import com.example.language_learning.services.states.AIGenerationState;
import com.example.language_learning.utils.AIResponseSanitizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class AIGenerationActions {
    private final AIResponseSanitizer sanitizer;
    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory jsonSchemaFactory;

    public Mono<AIGenerationState> handleGeneration(AIGenerationState fromState, AIGenerationContext context) {
        int attempt = context.attemptCounter().get();
        String userMessage = buildUserMessage(context.aiPrompt(), context.params());
        PromptType promptType = (PromptType) context.params().get("promptType");
        log.debug("Rendered Prompt for {} (Attempt {}): {}", promptType, attempt, userMessage);

        return context.chatClient().prompt()
                .user(userMessage)
                .stream()
                .content()
                .collectList()
                .map(list -> String.join("", list).trim())
                .doOnNext(rawResponse -> log.info("Raw AI Response for {} (Attempt {}): {}", promptType, attempt, rawResponse))
                .map(AIGenerationState.GENERATION::new);
    }

    public Mono<AIGenerationState> handleValidation(AIGenerationState fromState, AIGenerationContext context) {
        String rawResponse = ((AIGenerationState.GENERATION) fromState).rawResponse();
        String jsonString = sanitizer.extractAndSanitizeJson(rawResponse);
        int attempt = context.attemptCounter().get();
        PromptType promptType = (PromptType) context.params().get("promptType");
        log.debug("Extracted JSON for {} (Attempt {}): {}", promptType, attempt, jsonString);

        try { // First, try to parse the JSON
            JsonNode responseNode = objectMapper.readTree(jsonString);
            // If parsing succeeds, proceed to schema validation
            JsonSchema schema = jsonSchemaFactory.getSchema(context.aiPrompt().schema());
            Set<ValidationMessage> errors = schema.validate(responseNode);

            if (!errors.isEmpty()) {
                String errorDetails = errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining(", "));
                log.warn("Attempt {} for {} failed schema validation: {}", attempt, promptType, errorDetails);
                context.params().put("invalidJson", jsonString);
                context.params().put("validationFeedback", "Your previous response failed schema validation with the following errors: " + errorDetails + ". You MUST fix these errors.");
            }

            // Always return a VALIDATION state, which holds the parsed node and any errors (which might be an empty set)
            return Mono.just(new AIGenerationState.VALIDATION(responseNode, errors));
        }
        catch (JsonProcessingException e) { // If parsing fails, go directly to retry
            log.warn("Attempt {} for {} failed due to a JSON processing error: {}", context.attemptCounter().get(), promptType, e.getMessage());
            context.params().put("invalidJson", jsonString);
            context.params().put("validationFeedback", "Your previous response could not be parsed as valid JSON. It might be malformed or incomplete. You MUST provide a complete and valid JSON object that strictly adheres to the schema.");
            return Mono.just(new AIGenerationState.RETRYING("JSON processing error"));
        }
    }

    public Mono<AIGenerationState> handleValidationCompletion(AIGenerationState fromState, AIGenerationContext context) {
        JsonNode responseNode = ((AIGenerationState.VALIDATION) fromState).responseNode();
        try {
            Object result = objectMapper.convertValue(responseNode, context.apiDtoType());
            return Mono.just(new AIGenerationState.COMPLETED(result));
        }
        catch (Exception e) {
            log.error("Failed to convert validated JSON to DTO: {}", e.getMessage());
            return Mono.just(new AIGenerationState.FAILED("DTO conversion failed after validation."));
        }
    }

    public Mono<AIGenerationState> handleSanitization(AIGenerationState fromState, AIGenerationContext context) {
        AIGenerationState.VALIDATION validationState = (AIGenerationState.VALIDATION) fromState;
        JsonNode responseNode = validationState.responseNode();
        Set<ValidationMessage> errors = validationState.errors();
        PromptType promptType = (PromptType) context.params().get("promptType");
        JsonSchema schema = jsonSchemaFactory.getSchema(context.aiPrompt().schema());
        JsonNode fixedNode = sanitizer.sanitizeJsonValidationErrors(responseNode, errors, schema);

        if (fixedNode != responseNode) {
            log.info("Sanitization applied for {}. Re-evaluating result.", promptType);
        }
        else {
            log.info("Sanitization was not applicable for {}. Proceeding with original errors.", promptType);
        }

        // After sanitizing we revalidate the sanitized node, and then we create a SANITIZING state that holds the result of the attempt.
        Set<ValidationMessage> newErrors = schema.validate(fixedNode);
        return Mono.just(new AIGenerationState.SANITIZING(fixedNode, newErrors));
    }

    public Mono<AIGenerationState> handleSanitizationCompletion(AIGenerationState fromState, AIGenerationContext context) {
        PromptType promptType = (PromptType) context.params().get("promptType");
        JsonNode responseNode = ((AIGenerationState.SANITIZING) fromState).responseNode();
        try {
            log.info("Sanitization successful! {} passed schema validation after fix.", promptType);
            Object result = objectMapper.convertValue(responseNode, context.apiDtoType());
            return Mono.just(new AIGenerationState.COMPLETED(result));
        }
        catch (Exception e) {
            log.error("Failed to convert sanitized JSON to DTO: {}", e.getMessage());
            return Mono.just(new AIGenerationState.FAILED("DTO conversion failed after sanitization."));
        }
    }

    public Mono<AIGenerationState> prepareForRetry(AIGenerationState fromState, AIGenerationContext context) {
        Set<ValidationMessage> errors = ((AIGenerationState.SANITIZING) fromState).originalErrors();
        String errorDetails = errors.stream().map(ValidationMessage::getMessage).collect(Collectors.joining(", "));
        log.warn("Sanitization attempt failed. Final errors: {}", errorDetails);
        context.params().put("validationFeedback", "After attempting to sanitize, your response still has errors: " + errorDetails + ". You MUST fix these errors.");
        return Mono.just(new AIGenerationState.RETRYING("Proceeding to retry check after failed sanitization."));
    }

    public Mono<AIGenerationState> handleRetry(AIGenerationState fromState, AIGenerationContext context) {
        if (context.attemptCounter().incrementAndGet() > context.maxRetries()) {
            PromptType promptType = (PromptType) context.params().get("promptType");
            String reason = String.format("AI response validation failed after %d retries for prompt type: %s.", context.maxRetries(), promptType);
            return Mono.just(new AIGenerationState.FAILED(reason));
        }
        return Mono.just(new AIGenerationState.IDLE());
    }



    private String renderPrompt(Resource resource, Map<String, Object> params) {
        try {
            String templateString = resource.getContentAsString(StandardCharsets.UTF_8);
            PromptTemplate promptTemplate = new PromptTemplate(templateString);
            return promptTemplate.render(params);
        }
        catch (Exception e) {
            log.error("Failed to render prompt template from resource: {}.", resource.getFilename(), e);
            throw new IllegalArgumentException("Invalid prompt template rendering: " + resource.getFilename(), e);
        }
    }

    private String buildUserMessage(AIConfig.AIPrompt aiPrompt, Map<String, Object> params) {
        String instructionContent = renderPrompt(aiPrompt.instruction(), params);

        // Check if this is a retry attempt and add specific feedback
        if (params.containsKey("validationFeedback")) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Your previous response was invalid. You MUST correct it. ");
            messageBuilder.append(params.get("validationFeedback"));
            if (params.containsKey("invalidJson")) {
                messageBuilder.append("\n\nInvalid JSON Response:\n```json\n")
                        .append(params.get("invalidJson"))
                        .append("\n```");
            }
            messageBuilder.append("\n\n--- Original Instructions ---\n");
            messageBuilder.append(instructionContent); // Re-include original instructions
            return messageBuilder.toString();
        }

        // For the initial request, send instructions AND schema.
        JsonNode schemaNode = aiPrompt.schema();
        String schemaContent = (schemaNode != null) ? schemaNode.toPrettyString() : "{}";
        return instructionContent +
                "\n\n## JSON Schema\nYour output MUST conform to the following JSON schema:\n```json\n" +
                schemaContent + "\n```";
    }
}

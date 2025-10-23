package com.example.language_learning.ai.actions;

import com.example.language_learning.ai.config.model.AIPrompt;
import com.example.language_learning.ai.enums.PromptType;
import com.example.language_learning.ai.services.ContentModerationService;
import com.example.language_learning.shared.services.FuriganaService;
import com.example.language_learning.ai.contexts.AIGenerationContext;
import com.example.language_learning.ai.states.AIGenerationState;
import com.example.language_learning.shared.utils.AIResponseSanitizer;
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
    private final ContentModerationService moderationService;
    private final AIResponseSanitizer sanitizer;
    private final FuriganaService furiganaService;
    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory jsonSchemaFactory;

    public Mono<AIGenerationState> handleModeration(AIGenerationState fromState, AIGenerationContext context) {
        if (!context.withModeration()) {
            log.debug("Moderation not requested for this AI request. Skipping...");
            return Mono.just(AIGenerationState.GENERATION);
        }

        String textToModerate = getTextToModerate(context.params());
        log.debug("Entering handleModeration to check user message for safety.");

        if (textToModerate == null || textToModerate.isBlank()) {
            log.debug("Text to moderate not found...skipping moderation check.");
            return Mono.just(AIGenerationState.GENERATION);
        }

        return moderationService.isContentFlagged(textToModerate)
                .flatMap(isFlagged -> {
                    if (Boolean.TRUE.equals(isFlagged)) {
                        log.warn("Content moderation failed for the provided text: '{}'.", textToModerate);
                        String reason = "The provided text does not meet our safety guidelines.";
                        return Mono.just(AIGenerationState.FAILED(reason, null));
                    }
                    log.info("Content moderation passed.");
                    return Mono.just(AIGenerationState.GENERATION);
                });
    }

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
                .map(AIGenerationState::VALIDATION);
    }

    public Mono<AIGenerationState> handleValidation(AIGenerationState fromState, AIGenerationContext context) {
        String rawResponse = ((AIGenerationState.VALIDATION) fromState).rawResponse();
        String jsonString = sanitizer.repairJson(rawResponse);
        int attempt = context.attemptCounter().get();
        PromptType promptType = (PromptType) context.params().get("promptType");
        log.debug("Extracted JSON for {} (Attempt {}): {}", promptType, attempt, jsonString);

        try { // First, try to parse the JSON
            JsonNode responseNode = objectMapper.readTree(jsonString);
            // If parsing succeeds, proceed to schema validation

            // Dynamically apply pre-validation sanitization for specific prompt types
            if (promptType == PromptType.VOCABULARY_LESSON && "japanese".equalsIgnoreCase(context.language())) {
                responseNode = furiganaService.sanitizeJapaneseVocabularyNode(responseNode);
            }

            JsonSchema schema = jsonSchemaFactory.getSchema(context.aiPrompt().schema());
            Set<ValidationMessage> errors = schema.validate(responseNode);

            if (errors.isEmpty()) {
                // If validation passes, we are done. Convert to DTO and complete.
                log.info("Attempt {} for {} passed schema validation.", attempt, promptType);
                Object result = objectMapper.convertValue(responseNode, context.apiDtoType());
                return Mono.just(AIGenerationState.COMPLETED(result));
            }
            else {
                // If validation fails, the next state is SANITIZING, which needs the node and errors.
                String errorDetails = errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining(", "));
                log.warn("Attempt {} for {} failed schema validation: {}", attempt, promptType, errorDetails);
                context.params().put("invalidJson", jsonString);
                context.params().put("validationFeedback", "Your previous response failed schema validation with the following errors: " + errorDetails + ". You MUST fix these errors.");
                return Mono.just(AIGenerationState.SANITIZING(responseNode, errors));
            }
        }
        catch (JsonProcessingException e) { // If parsing fails, go directly to retry
            log.warn("Attempt {} for {} failed due to a JSON processing error: {}", context.attemptCounter().get(), promptType, e.getMessage());
            context.params().put("invalidJson", jsonString);
            context.params().put("validationFeedback", "Your previous response could not be parsed as valid JSON. It might be malformed or incomplete. You MUST provide a complete and valid JSON object that strictly adheres to the schema.");
            return Mono.just(AIGenerationState.RETRYING);
        }
    }

    public Mono<AIGenerationState> handleSanitization(AIGenerationState fromState, AIGenerationContext context) {
        // 1. Correctly cast to SANITIZING to get the required input data.
        AIGenerationState.SANITIZING sanitizingState = (AIGenerationState.SANITIZING) fromState;
        JsonNode responseNode = sanitizingState.responseNode();
        Set<ValidationMessage> errors = sanitizingState.errors();
        PromptType promptType = (PromptType) context.params().get("promptType");
        JsonSchema schema = jsonSchemaFactory.getSchema(context.aiPrompt().schema());

        // 2. Perform the sanitization and re-validation.
        JsonNode fixedNode = sanitizer.sanitizeJsonValidationErrors(responseNode, errors, schema);
        Set<ValidationMessage> newErrors = schema.validate(fixedNode);

        // 3. Decide the next state based on the sanitization outcome.
        if (newErrors.isEmpty()) {
            // If sanitization succeeds, we are done. Convert to DTO and complete.
            log.info("Sanitization successful! {} passed schema validation after fix.", promptType);
            try {
                Object result = objectMapper.convertValue(fixedNode, context.apiDtoType());
                return Mono.just(AIGenerationState.COMPLETED(result));
            }
            catch (Exception e) {
                log.error("Failed to convert sanitized JSON to DTO: {}", e.getMessage());
                return Mono.just(AIGenerationState.FAILED("DTO conversion failed after sanitization.", e));
            }
        }
        else {
            // If sanitization fails, prepare feedback and go to the RETRYING state.
            String errorDetails = newErrors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.joining(", "));
            log.warn("Sanitization attempt failed for {}. Final errors: {}", promptType, errorDetails);
            context.params().put("validationFeedback", "After attempting to sanitize, your response still has errors: " + errorDetails + ". You MUST fix these errors.");
            return Mono.just(AIGenerationState.RETRYING);
        }
    }

    public Mono<AIGenerationState> handleRetry(AIGenerationState fromState, AIGenerationContext context) {
        if (context.attemptCounter().get() - 1 >= context.maxRetries()) {
            PromptType promptType = (PromptType) context.params().get("promptType");
            String reason = String.format("AI response validation failed after %d retries for prompt type: %s.", context.maxRetries(), promptType);
            return Mono.just(new AIGenerationState.FAILED(reason, null));
        }
        context.attemptCounter().incrementAndGet();
        return Mono.just(AIGenerationState.GENERATION);
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

    private String buildUserMessage(AIPrompt aiPrompt, Map<String, Object> params) {
        String instructionContent = renderPrompt(aiPrompt.instruction(), params);

        JsonNode schemaNode = aiPrompt.schema();
        String schemaContent = (schemaNode != null) ? schemaNode.toPrettyString() : "{}";

        // Check if this is a retry attempt and add specific feedback
        if (params.containsKey("validationFeedback")) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Your previous response was invalid. You MUST provide a corrected JSON object that fixes the specified errors. ");
            messageBuilder.append(params.get("validationFeedback"));
            if (params.containsKey("invalidJson")) {
                messageBuilder.append("\n\nInvalid JSON Response:\n```json\n")
                        .append(params.get("invalidJson"))
                        .append("\n```");
            }
            messageBuilder.append("\n\nPlease review your original instructions, the errors, and the invalid JSON, then provide a new, complete, and valid JSON object. Do not include any extra text or explanations outside of the JSON object itself.");
            messageBuilder.append("\n\n--- Original Instructions & Schema ---\n").append(instructionContent);
            messageBuilder.append("\n\n## JSON Schema\nYour output MUST conform to the following JSON schema:\n```json\n")
                    .append(schemaContent)
                    .append("\n```");
            return messageBuilder.toString();
        }

        // For the initial request, send instructions AND schema.
        return instructionContent +
                "\n\n## JSON Schema\nYour output MUST conform to the following JSON schema:\n```json\n" +
                schemaContent + "\n```";
    }

    private String getTextToModerate(Map<String, Object> params) {
        PromptType promptType = (PromptType) params.get("promptType");
        return switch (promptType) {
            case STORY_METADATA, LESSON_METADATA -> (String) params.get("topic");
            case PROOFREAD -> (String) params.get("sentence");
            case TRANSLATE -> (String) params.get("textToTranslate");
            // If a new prompt type needs moderation, add its case here.
            default -> null;
        };
    }
}

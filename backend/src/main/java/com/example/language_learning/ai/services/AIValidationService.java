package com.example.language_learning.ai.services;

import com.example.language_learning.ai.dtos.embedding.ScoredChoice;
import com.example.language_learning.ai.embedding.EmbeddingService;
import com.example.language_learning.ai.embedding.EmbeddingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "djl.embedding.enabled", havingValue = "true", matchIfMissing = true)
public class AIValidationService {
    private final EmbeddingService embeddingService;

    @Value("${djl.embedding.similarity-threshold}")
    private double similarityThreshold;

    /**
     * Finds the best matching answer from a list of choices.
     * It first attempts an exact match. If none is found, it uses semantic similarity
     * to find the closest match.
     *
     * @param targetAnswer The answer string provided by the AI.
     * @param answerChoices The list of possible answer choices.
     * @return The best matching string from the answerChoices list.
     */
    public String findBestMatchingAnswer(String targetAnswer, List<String> answerChoices) {
        if (targetAnswer == null || targetAnswer.isBlank() || answerChoices == null || answerChoices.isEmpty()) {
            return targetAnswer;
        }

        // 1. First, try for an exact match (most common and reliable case)
        if (answerChoices.contains(targetAnswer)) {
            return targetAnswer;
        }

        // 2. If no exact match, use semantic similarity as a fallback
        log.warn("No exact match for answer '{}'. Using semantic search.", targetAnswer);

        try {
            float[] targetEmbedding = embeddingService.getEmbedding(targetAnswer);
            // Map each choice to a ScoredChoice object, then find the one with the max score.
            // This ensures getEmbedding is called only once per choice.
            return answerChoices.stream()
                    .map(choice -> {
                        try {
                            float[] choiceEmbedding = embeddingService.getEmbedding(choice);
                            double score = EmbeddingUtils.cosineSimilarity(targetEmbedding, choiceEmbedding);
                            return new ScoredChoice(choice, score);
                        }
                        catch (Exception e) {
                            log.error("Error getting embedding for choice: '{}'", choice, e);
                            return new ScoredChoice(choice, -1.0);
                        }
                    })
                    .max(Comparator.comparingDouble(ScoredChoice::score))
                    .map(ScoredChoice::choice)
                    .orElse(targetAnswer);
        }
        catch (Exception e) {
            log.error("Failed to get embedding for target answer: '{}'", targetAnswer, e);
            return targetAnswer;
        }
    }
}

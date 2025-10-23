package com.example.language_learning.ai.embedding;

import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.example.language_learning.ai.config.EmbeddingConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "djl.embedding.enabled", havingValue = "true", matchIfMissing = true)
public class EmbeddingService {

    private final EmbeddingConfig embeddingConfig;
    private final Criteria<String, float[]> sentenceEmbeddingCriteria;
    private ZooModel<String, float[]> model;
    private Predictor<String, float[]> predictor;

    @PostConstruct
    public void init() throws Exception {
        log.info("Loading sentence embedding model from: {}", embeddingConfig.getModelUrl());
        this.model = sentenceEmbeddingCriteria.loadModel();
        this.predictor = model.newPredictor();
        log.info("Sentence embedding model loaded successfully.");
    }


    /**
     * Generates a vector embedding for the given text.
     * This method is synchronized to ensure thread-safe access to the non-thread-safe Predictor.
     * @param text The input text.
     * @return A float array representing the vector embedding.
     * @throws TranslateException if an error occurs during the prediction.
     */
    public synchronized float[] getEmbedding(String text) throws TranslateException {
        return predictor.predict(text);
    }

    /**
     * Generates vector embeddings for a batch of texts.
     * This method is synchronized to ensure thread-safe access to the non-thread-safe Predictor.
     * @param texts The list of input texts.
     * @return A list of float arrays representing the vector embeddings.
     * @throws TranslateException if an error occurs during the prediction.
     */
    public synchronized List<float[]> getEmbeddings(List<String> texts) throws TranslateException {
        return predictor.batchPredict(texts);
    }

    @PreDestroy
    public void destroy() {
        log.info("Closing sentence embedding model resources.");
        if (predictor != null) {
            predictor.close();
        }
        if (model != null) {
            model.close();
        }
    }
}

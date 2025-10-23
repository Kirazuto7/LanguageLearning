package com.example.language_learning.ai.embedding;

public final class EmbeddingUtils {
    private EmbeddingUtils() {}


    /**
     * Calculates the cosine similarity between two vectors.
     * A value of 1 means the vectors are identical, 0 means they are orthogonal (unrelated),
     * and -1 means they are diametrically opposed.
     *
     * @param vectorA The first vector.
     * @param vectorB The second vector.
     * @return The cosine similarity, a value between -1 and 1.
     */
    public static double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        if (denominator == 0.0) {
            return 0.0;
        }

        return dotProduct / denominator;
    }
}

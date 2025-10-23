package com.example.language_learning.shared.services;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.example.language_learning.ai.dtos.details.AIJapaneseVocabularyItemDTO;
import com.example.language_learning.shared.exceptions.JsonNodeProcessingException;
import com.example.language_learning.shared.word.dtos.JapaneseWordDetailsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;
import java.util.regex.Pattern;

@Service
public class FuriganaService {

    private final Tokenizer tokenizer;
    private final ObjectMapper objectMapper;
    private static final Pattern KANJI_PATTERN = Pattern.compile(".*\\p{sc=Han}.*");
    private static final Pattern HIRAGANA_PATTERN = Pattern.compile("^[\\p{sc=Hiragana}ー]+$");
    private static final Pattern KATAKANA_PATTERN = Pattern.compile("^[\\p{sc=Katakana}ー]+$");

    public FuriganaService(Tokenizer tokenizer, ObjectMapper objectMapper) {
        this.tokenizer = tokenizer;
        this.objectMapper = objectMapper;
    }

    public String addFurigana(String text) {
        if(text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        List<Token> tokens = tokenizer.tokenize(text);

        for(Token token : tokens) {
            String surface = token.getSurface();
            String hiraganaReading = katakanaToHiragana(token.getReading());

            if(KANJI_PATTERN.matcher(surface).matches() && hiraganaReading != null && !surface.equals(hiraganaReading)) {
                result.append("<ruby>").append(surface).append("<rt>").append(hiraganaReading).append("</rt></ruby>");
            } else {
                result.append(surface);
            }
        }
        return result.toString();
    }

    /**
     * A pre-validation sanitizer for Japanese vocabulary JSON. It iterates through vocabulary items,
     * uses the tokenizer to fix inconsistencies (like romaji in hiragana fields), and returns a
     * cleaned JsonNode ready for schema validation.
     *
     * @param rootNode The raw, parsed JsonNode from the AI.
     * @return A new, cleaned JsonNode.
     */
    public JsonNode sanitizeJapaneseVocabularyNode(JsonNode rootNode) {
        if (!rootNode.has("vocabularies") || !rootNode.get("vocabularies").isArray()) {
            return rootNode;
        }

        ArrayNode vocabulariesNode = (ArrayNode) rootNode.get("vocabularies");
        ArrayNode sanitizedVocabularies = objectMapper.createArrayNode();

        for (JsonNode vocabNode : vocabulariesNode) {
            try {
                AIJapaneseVocabularyItemDTO aiWord = objectMapper.treeToValue(vocabNode, AIJapaneseVocabularyItemDTO.class);
                JapaneseWordDetailsDTO correctedDetails = verifyAndMapJapaneseWord(aiWord);

                // Reconstruct the node to match the flat schema structure, preserving the englishTranslation
                ObjectNode sanitizedNode = objectMapper.valueToTree(correctedDetails);
                if (vocabNode.has("englishTranslation")) {
                    sanitizedNode.put("englishTranslation", vocabNode.get("englishTranslation").asText());
                }

                sanitizedVocabularies.add(sanitizedNode);
            } catch (JsonProcessingException e) {
                throw new JsonNodeProcessingException("Failed to process vocabulary node during sanitization", e);
            }
        }

        ((ObjectNode) rootNode).set("vocabularies", sanitizedVocabularies);
        return rootNode;
    }

    /**
     * Verifies and maps a Japanese vocabulary item from the AI Response.
     * Utilizing the Kuromoji tokenizer we can derive the proper forms of (Kanji, Katakana) from hiragana
     * to combat AI inconsistencies and protect data integrity.
     */
    public JapaneseWordDetailsDTO verifyAndMapJapaneseWord(AIJapaneseVocabularyItemDTO aiWord) {
        // 1. First verify AI output with REGEX
        String hiragana = patternVerifier(aiWord.hiragana(), HIRAGANA_PATTERN);
        String katakana = patternVerifier(aiWord.katakana(), KATAKANA_PATTERN);
        String kanji = patternVerifier(aiWord.kanji(), KANJI_PATTERN);

        // No Data to work with so return null
        if (hiragana == null && katakana == null && kanji == null) { return null; }

        // 2. If any of the individual outputs are incorrect, attempt to sanitize with the tokenizer
        String inputForTokenizer = Stream.of(kanji, hiragana, katakana)
                .filter(word -> word != null && !word.isBlank())
                .findFirst()
                .orElse(null);

        if(inputForTokenizer == null) return null;

        List<Token> tokens = tokenizer.tokenize(inputForTokenizer);
        StringBuilder kanjiBuilder = new StringBuilder();
        StringBuilder hiraganaBuilder = new StringBuilder();
        StringBuilder katakanaBuilder = new StringBuilder();

        for(Token token : tokens) {
            String surfaceForm = token.getSurface();
            String readingForm = token.getReading(); // Katakana

            kanjiBuilder.append(surfaceForm);

            if(readingForm != null) {
                hiraganaBuilder.append(katakanaToHiragana(readingForm));
                katakanaBuilder.append(readingForm);
            }
            else {
                hiraganaBuilder.append(surfaceForm);
                katakanaBuilder.append(surfaceForm);
            }
        }

        if(kanji == null) {
            kanji = kanjiBuilder.toString();
        }

        if(hiragana == null) {
            hiragana = hiraganaBuilder.toString();
        }

        if(katakana == null) {
            katakana = katakanaBuilder.toString();
        }

        // 3. Build and return the word object
        return JapaneseWordDetailsDTO.builder()
                .kanji(kanji)
                .hiragana(hiragana)
                .katakana(katakana)
                .romaji(aiWord.romaji())
                .build();
    }

    private String katakanaToHiragana(String katakana) {
        if (katakana == null) return null;
        StringBuilder hiragana = new StringBuilder();
        for (char c : katakana.toCharArray()) {
            if (c >= 'ァ' && c <= 'ヶ') {
                hiragana.append((char) (c - 'ァ' + 'ぁ'));
            } else {
                hiragana.append(c);
            }
        }
        return hiragana.toString();
    }

    private String patternVerifier(String word, Pattern pattern) {
        if(word != null && !word.isBlank() && pattern.matcher(word).matches()) {
            return word;
        }
        return null;
    }
}

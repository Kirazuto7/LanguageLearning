package com.example.language_learning.shared.services;

import com.example.language_learning.ai.enums.Language;
import com.example.language_learning.shared.utils.LanguageAnalyzerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NlpService {
    private final LanguageAnalyzerFactory analyzerFactory;

    // Get a word's dictionary form (lemma) utilizing Lucene Analyzer
    public String getLemma(String word, Language language) {
        Analyzer analyzer = analyzerFactory.getAnalyzer(language);
        try (TokenStream stream = analyzer.tokenStream("lemmaField", word)) {
            // Get the attribute that holds the text of the token
            CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);

            // Reset the stream before you start consuming tokens
            stream.reset();

            // We only expect one token since we are analyzing a single word.
            if (stream.incrementToken()) {
                String lemma = termAttribute.toString();
                stream.end(); // Finalized the stream processing
                return lemma;
            }
        }
        catch (IOException e) {
            log.error("Error analyzing word '{}' for language {}", word, language.getValue(), e);
        }
        return word;
    }


    /**
     * Processes a list of words and returns their corresponding lemmas.
     *
     * @param words The list of words to analyze.
     * @param language The language of the words.
     * @return A list of lemmas in the same order as the input words.
     */
    public List<String> getLemmas(List<String> words, Language language) {
        if (words == null || words.isEmpty()) {
            return List.of();
        }

        return words.stream()
                .map(word -> getLemma(word, language))
                .collect(Collectors.toList());
    }
}

package com.example.language_learning.shared.services;

import com.example.language_learning.ai.enums.Language;
import com.example.language_learning.shared.utils.LanguageAnalyzerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.tokenattributes.ReadingAttribute;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class KoreanNlpService {

    private final LanguageAnalyzerFactory analyzerFactory;

    /**
     * Gets the Hanja (Chinese characters) for a given Korean word (Hangul).
     *
     * @param hangul The Korean word in Hangul.
     * @return The corresponding Hanja, or the original Hangul if no Hanja is found.
     */
    public String getHanja(String hangul) {
        if (hangul == null || hangul.isBlank()) {
            return hangul;
        }

        Analyzer analyzer = analyzerFactory.getAnalyzer(Language.KOREAN);
        try (TokenStream tokenStream = analyzer.tokenStream("field", hangul)) {
            ReadingAttribute readingAttribute = tokenStream.addAttribute(ReadingAttribute.class);
            tokenStream.reset();

            if (tokenStream.incrementToken()) {
                String hanja = readingAttribute.getReading();
                if (hanja != null && !hanja.equals(hangul)) {
                    return hanja;
                }
            }
            tokenStream.end();
        }
        catch (IOException e) {
            log.error("Failed to get Hanja for word: {}", hangul, e);
        }
        return  null;
    }
}

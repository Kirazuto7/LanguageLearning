package com.example.language_learning.shared.utils;

import com.example.language_learning.ai.enums.Language;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@Slf4j
public class LanguageAnalyzerFactory {

    private final Map<Language, Analyzer> analyzers = new EnumMap<>(Language.class);

    public LanguageAnalyzerFactory() {
        analyzers.put(Language.JAPANESE, new JapaneseAnalyzer());
        analyzers.put(Language.KOREAN, new KoreanAnalyzer());
        analyzers.put(Language.CHINESE, new SmartChineseAnalyzer());
        analyzers.put(Language.THAI, new ThaiAnalyzer());
        analyzers.put(Language.ITALIAN, new ItalianAnalyzer());
        analyzers.put(Language.SPANISH, new SpanishAnalyzer());
        analyzers.put(Language.FRENCH, new FrenchAnalyzer());
        analyzers.put(Language.GERMAN, new GermanAnalyzer());
    }

    public Analyzer getAnalyzer(Language language) {
        Analyzer analyzer = analyzers.get(language);
        if (analyzer == null) {
            log.error("No analyzer found for language: {}", language);
            throw new IllegalArgumentException("No analyzer found for language: " + language);
        }
        return analyzer;
    }
}

package com.example.language_learning.services;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FuriganaService {
    private final Tokenizer tokenizer;
    // Regex pattern to match kanji characters
    private static final Pattern KANJI_PATTERN = Pattern.compile(".*\\p{InCJK_UNIFIED_IDEOGRAPHS}.*");

    public String addFurigana(String text) {
        if(text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        List<Token> tokens = tokenizer.tokenize(text);

        for(Token token : tokens) {
            String surface = token.getSurface();
            String reading = token.getReading();

            if(KANJI_PATTERN.matcher(surface).matches() && reading != null && !surface.equals(reading)) {
                result.append("<ruby>").append(surface).append("<rt>").append(reading).append("</rt></ruby>");
            } else {
                result.append(surface);
            }
        }
        return result.toString();
    }
}

package com.example.language_learning.shared.services;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChineseNlpService {

    public String verifyTraditional(String simplified, String aiTraditional) {
        if (simplified == null || simplified.isBlank()) {
            return aiTraditional;
        }

        String convertedTraditional = ZhConverterUtil.toTraditional(simplified);

        if (convertedTraditional == null || convertedTraditional.isBlank()) {
            log.warn("OpenCC failed to convert simplified character '{}'. Falling back to AI's traditional form ('{}').", simplified, aiTraditional);
            return aiTraditional;
        }

        if (!convertedTraditional.equals(aiTraditional)) {
            log.warn("OpenCC conversion ('{}') mismatches AI's traditional form ('{}') for simplified character '{}'. Using OpenCC version for consistency.", convertedTraditional, aiTraditional, simplified);
        }

        return convertedTraditional;
    }
}

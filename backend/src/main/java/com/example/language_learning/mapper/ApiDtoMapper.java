package com.example.language_learning.mapper;

import com.example.language_learning.dto.api.*;
import com.example.language_learning.dto.languages.WordDTO;
import com.example.language_learning.dto.lessons.GrammarLessonDTO;
import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import com.example.language_learning.dto.lessons.ReadingComprehensionLessonDTO;
import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.dto.models.QuestionDTO;
import com.example.language_learning.entity.models.QuestionType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Maps from API-specific DTOs (from the Anti-Corruption Layer) to the
 * application's internal DTOs.
 */
@Component
public class ApiDtoMapper {

    public ChapterMetadataDTO toChapterMetadataDTO(AIChapterMetadataResponse response, String topic) {
        ChapterMetadataDTO dto = new ChapterMetadataDTO();
        dto.setTitle(response.title());
        dto.setNativeTitle(response.nativeTitle());
        dto.setTopic(topic);
        return dto;
    }

    public VocabularyLessonDTO toVocabularyLessonDTO(AIVocabularyLessonResponse response, String language) {
        VocabularyLessonDTO dto = new VocabularyLessonDTO();
        dto.setTitle(response.title());
        if(response.vocabularies() != null) {
            List<WordDTO> vocabularyItemDtos = response.vocabularies().stream()
                    .map(aiVocabulary -> {
                        WordDTO word = new WordDTO();
                        word.setNativeWord(aiVocabulary.nativeWord());
                        word.setPhoneticSpelling(aiVocabulary.phoneticSpelling());
                        word.setEnglishTranslation(aiVocabulary.englishTranslation());
                        word.setLanguage(language);
                        return word;
                    }).toList();
            dto.setVocabularies(vocabularyItemDtos);
        }
        return dto;
    }

    public GrammarLessonDTO toGrammarLessonDTO(AIGrammarLessonResponse response) {
        GrammarLessonDTO dto = new GrammarLessonDTO();
        dto.setTitle(response.title());
        dto.setGrammarConcept(response.grammarConcept());
        dto.setNativeGrammarConcept(response.nativeGrammarConcept());
        dto.setExplanation(response.explanation());
        dto.setExampleSentences(response.exampleSentences());
        return dto;
    }

    public PracticeLessonDTO toPracticeLessonDTO(AIPracticeLessonResponse response) {
        PracticeLessonDTO dto = new PracticeLessonDTO();
        dto.setTitle(response.title());
        dto.setInstructions(response.instructions());
        if(response.questions() != null) {
            List<QuestionDTO> questionDTOs = response.questions().stream()
                    .map(aiQuestion -> {
                        QuestionDTO questionDTO = new QuestionDTO();
                        questionDTO.setQuestionText(aiQuestion.questionText());
                        questionDTO.setQuestionType(QuestionType.FREE_FORM.name());
                        questionDTO.setAnswer(null);
                        questionDTO.setOptions(Collections.emptyList());
                        return questionDTO;
                    }).toList();
            dto.setQuestions(questionDTOs);
        }
        return dto;
    }

    public ReadingComprehensionLessonDTO toReadingComprehensionLessonDTO(AIReadingComprehensionLessonResponse response) {
        ReadingComprehensionLessonDTO dto = new ReadingComprehensionLessonDTO();
        dto.setTitle(response.title());
        dto.setStory(response.story());
        if(response.questions() != null) {
            List<QuestionDTO> questionDTOs = response.questions().stream()
                    .map(aiQuestion -> {
                        QuestionDTO questionDTO = new QuestionDTO();
                        questionDTO.setQuestionText(aiQuestion.questionText());
                        questionDTO.setQuestionType(QuestionType.MULTIPLE_CHOICE.name());
                        questionDTO.setAnswer(aiQuestion.answer());
                        questionDTO.setOptions(aiQuestion.options());
                        return questionDTO;
                    }).toList();
            dto.setQuestions(questionDTOs);
        }
        return dto;
    }

}
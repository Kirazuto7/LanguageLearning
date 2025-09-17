package com.example.language_learning.mappers;

import com.example.language_learning.user.dtos.SettingsDTO;
import com.example.language_learning.user.dtos.UserDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos.WordDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.dtos.QuestionDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.dtos.ConjugationExampleDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.dtos.SentenceDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos.WordDetailsDTO;
import com.example.language_learning.user.data.Settings;
import com.example.language_learning.user.data.User;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.data.ConjugationExample;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.data.Word;
import com.example.language_learning.lessonbook.chapter.data.Chapter;
import com.example.language_learning.lessonbook.chapter.dtos.ChapterDTO;
import com.example.language_learning.lessonbook.chapter.lesson.data.ConjugationLesson;
import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.lessonbook.chapter.lesson.dtos.LessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.data.Page;
import com.example.language_learning.lessonbook.chapter.lesson.page.dtos.PageDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.data.Question;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.data.Sentence;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.data.WordDetails;
import com.example.language_learning.lessonbook.data.LessonBook;
import com.example.language_learning.lessonbook.dtos.LessonBookDTO;
import com.example.language_learning.mappers.mapstruct.*;
import com.example.language_learning.mappers.mapstruct.details.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class DtoMapper {
    private final UserStructMapper userStructMapper;
    private final SettingsStructMapper settingsStructMapper;
    private final LessonBookStructMapper lessonBookStructMapper;
    private final ChapterStructMapper chapterStructMapper;
    private final PageStructMapper pageStructMapper;
    private final LessonStructMapper lessonStructMapper;
    private final VocabularyLessonStructMapper vocabularyLessonStructMapper;
    private final GrammarLessonStructMapper grammarLessonStructMapper;
    private final ConjugationLessonStructMapper conjugationLessonStructMapper;
    private final PracticeLessonStructMapper practiceLessonStructMapper;
    private final ReadingComprehensionLessonStructMapper readingComprehensionLessonStructMapper;
    private final WordStructMapper wordStructMapper;
    private final WordDetailsStructMapper wordDetailsStructMapper;
    private final SentenceStructMapper sentenceStructMapper;
    private final QuestionStructMapper questionStructMapper;
    private final ConjugationExampleStructMapper conjugationExampleStructMapper;
    private final KoreanWordDetailsStructMapper koreanWordDetailsStructMapper;
    private final ChineseWordDetailsStructMapper chineseWordDetailsStructMapper;
    private final JapaneseWordDetailsStructMapper japaneseWordDetailsStructMapper;
    private final ThaiWordDetailsStructMapper thaiWordDetailsStructMapper;
    private final ItalianWordDetailsStructMapper italianWordDetailsStructMapper;
    private final SpanishWordDetailsStructMapper spanishWordDetailsStructMapper;
    private final FrenchWordDetailsStructMapper frenchWordDetailsStructMapper;
    private final GermanWordDetailsStructMapper germanWordDetailsStructMapper;

    /* ***************** */
    /* ** Book Mapper ** */
    /* ***************** */

    public LessonBook toEntity(LessonBookDTO dto) {
        return lessonBookStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public LessonBookDTO toDto(LessonBook entity) {
        return lessonBookStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ******************** */
    /* ** Chapter Mapper ** */
    /* ******************** */

    public Chapter toEntity(ChapterDTO dto) {
        return chapterStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public ChapterDTO toDto(Chapter entity) {
        return chapterStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ***************** */
    /* ** Page Mapper ** */
    /* ***************** */

    public Page toEntity(PageDTO dto) {
        return pageStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public PageDTO toDto(Page entity) {
        return pageStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ******************* */
    /* ** Lesson Mapper ** */
    /* ******************* */

    public Lesson toEntity(LessonDTO dto) {
        return lessonStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public LessonDTO toDto(Lesson entity) {
        return lessonStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ******************* */
    /* **  Word Mapper  ** */
    /* ******************* */

    public Word toEntity(WordDTO dto) {
        return wordStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public WordDetails toEntity(WordDetailsDTO dto) {
        return wordDetailsStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public WordDTO toDto(Word entity) {
        return wordStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    public WordDetailsDTO toDto(WordDetails entity) {
        return wordDetailsStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ********************* */
    /* ** Sentence Mapper ** */
    /* ********************* */

    public Sentence toEntity(SentenceDTO dto) {
        return sentenceStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public SentenceDTO toDto(Sentence entity) {
        return sentenceStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* *********************************** */
    /* **  Conjugation Example Mapper   ** */
    /* *********************************** */

    public ConjugationExample toEntity(ConjugationExampleDTO dto, ConjugationLesson lesson) {
        ConjugationExample example = conjugationExampleStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
        if (example != null) {
            example.setLesson(lesson);
        }
        return example;
    }

    public ConjugationExampleDTO toDto(ConjugationExample entity) {
        return conjugationExampleStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ************************ */
    /* **  Question Mapper   ** */
    /* ************************ */

    public Question toEntity(QuestionDTO dto, Lesson lesson) {
        Question question = questionStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
        if (question != null) {
            question.setLesson(lesson);
        }
        return question;
    }

    public QuestionDTO toDto(Question entity) {
        return questionStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* **************************** */
    /* **      User Mapper       ** */
    /* **************************** */

    public User toEntity(UserDTO dto) {
        return userStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public UserDTO toDto(User entity) {
        return userStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* **************************** */
    /* **    Settings Mapper     ** */
    /* **************************** */

    public Settings toEntity(SettingsDTO dto) {
        return settingsStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public SettingsDTO toDto(Settings entity) {
        return settingsStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

}
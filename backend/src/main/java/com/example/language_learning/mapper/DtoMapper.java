package com.example.language_learning.mapper;

import com.example.language_learning.dto.models.details.*;
import com.example.language_learning.dto.user.SettingsDTO;
import com.example.language_learning.dto.user.UserDTO;
import com.example.language_learning.dto.models.WordDTO;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.*;
import com.example.language_learning.entity.models.details.*;
import com.example.language_learning.entity.user.Settings;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.entity.models.Word;
import com.example.language_learning.entity.lessons.*;
import com.example.language_learning.entity.models.*;
import com.example.language_learning.mapper.mapstruct.*;
import com.example.language_learning.mapper.mapstruct.details.*;
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
    private final ReadingComprehensionStructMapper readingComprehensionStructMapper;
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
        return lessonBookStructMapper.toEntity(dto);
    }

    public LessonBookDTO toDto(LessonBook entity) {
        return lessonBookStructMapper.toDto(entity);
    }

    /* ******************** */
    /* ** Chapter Mapper ** */
    /* ******************** */

    public Chapter toEntity(ChapterDTO dto) {
        return chapterStructMapper.toEntity(dto);
    }

    public ChapterDTO toDto(Chapter entity) {
        return chapterStructMapper.toDto(entity);
    }

    /* ***************** */
    /* ** Page Mapper ** */
    /* ***************** */

    public Page toEntity(PageDTO dto) {
        return pageStructMapper.toEntity(dto);
    }

    public PageDTO toDto(Page entity) {
        return pageStructMapper.toDto(entity);
    }

    /* ******************* */
    /* ** Lesson Mapper ** */
    /* ******************* */

    public Lesson toEntity(LessonDTO dto) {
        return lessonStructMapper.toEntity(dto);
    }

    public LessonDTO toDto(Lesson entity) {
        return lessonStructMapper.toDto(entity);
    }

    /* ******************* */
    /* **  Word Mapper  ** */
    /* ******************* */

    public Word toEntity(WordDTO dto) {
        return wordStructMapper.toEntity(dto);
    }

    public WordDetails toEntity(WordDetailsDTO dto) {
        return wordDetailsStructMapper.toEntity(dto);
    }

    public WordDTO toDto(Word entity) {
        return wordStructMapper.toDto(entity);
    }

    public WordDetailsDTO toDto(WordDetails entity) {
        return wordDetailsStructMapper.toDto(entity);
    }

    /* ********************* */
    /* ** Sentence Mapper ** */
    /* ********************* */

    public Sentence toEntity(SentenceDTO dto) {
        return sentenceStructMapper.toEntity(dto);
    }

    public SentenceDTO toDto(Sentence entity) {
        return sentenceStructMapper.toDto(entity);
    }

    /* *********************************** */
    /* **  Conjugation Example Mapper   ** */
    /* *********************************** */

    public ConjugationExample toEntity(ConjugationExampleDTO dto, ConjugationLesson lesson) {
        return conjugationExampleStructMapper.toEntity(dto, lesson);
    }

    public ConjugationExampleDTO toDto(ConjugationExample entity) {
        return conjugationExampleStructMapper.toDto(entity);
    }

    /* ************************ */
    /* **  Question Mapper   ** */
    /* ************************ */

    public Question toEntity(QuestionDTO dto, Lesson lesson) {
        return questionStructMapper.toEntity(dto, lesson);
    }

    public QuestionDTO toDto(Question entity) {
        return questionStructMapper.toDto(entity);
    }

    /* **************************** */
    /* **      User Mapper       ** */
    /* **************************** */

    public User toEntity(UserDTO dto) {
        return userStructMapper.toEntity(dto);
    }

    public UserDTO toDto(User entity) {
        return userStructMapper.toDto(entity);
    }

    /* **************************** */
    /* **    Settings Mapper     ** */
    /* **************************** */

    public Settings toEntity(SettingsDTO dto) {
        return settingsStructMapper.toEntity(dto);
    }

    public SettingsDTO toDto(Settings entity) {
        return settingsStructMapper.toDto(entity);
    }

}
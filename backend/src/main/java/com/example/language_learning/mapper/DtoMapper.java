package com.example.language_learning.mapper;

import com.example.language_learning.dto.user.SettingsDTO;
import com.example.language_learning.dto.user.UserDTO;
import com.example.language_learning.dto.models.WordDTO;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.*;
import com.example.language_learning.entity.user.Settings;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.entity.models.Word;
import com.example.language_learning.entity.lessons.*;
import com.example.language_learning.entity.models.*;
import com.example.language_learning.enums.LessonType;
import com.example.language_learning.enums.QuestionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class DtoMapper {

    /* ***************** */
    /* ** Book Mapper ** */
    /* ***************** */

    public LessonBook toEntity(LessonBookDTO dto) {
        if (dto == null) return null;
        LessonBook book = LessonBook.builder()
                .bookTitle(dto.bookTitle())
                .difficulty(dto.difficulty())
                .language(dto.language())
                .build();

        if (dto.chapters() != null) {
            // Set the back-reference on each chapter
            dto.chapters().stream()
                    .map(this::toEntity)
                    .forEach(book::addChapter);
        }
        return book;
    }

    public LessonBookDTO toDto(LessonBook entity) {
        if (entity == null) return null;
        return LessonBookDTO.builder()
                .id(entity.getId())
                .bookTitle(entity.getBookTitle())
                .difficulty(entity.getDifficulty())
                .language(entity.getLanguage())
                .chapters(entity.getChapters().stream().map(this::toDto).toList())
                .build();
    }


    /* ******************** */
    /* ** Chapter Mapper ** */
    /* ******************** */

    public Chapter toEntity(ChapterDTO dto) {
        if (dto == null) return null;
        Chapter chapter = Chapter.builder()
                .chapterNumber(dto.chapterNumber())
                .title(dto.title())
                .nativeTitle(dto.nativeTitle())
                .build();

        if (dto.pages() != null) {
            dto.pages().stream()
                    .map(this::toEntity)
                    .forEach(chapter::addPage);
        }
        return chapter;
    }

    public ChapterDTO toDto(Chapter entity) {
        if (entity == null) return null;
        return ChapterDTO.builder()
                .id(entity.getId())
                .chapterNumber(entity.getChapterNumber())
                .title(entity.getTitle())
                .nativeTitle(entity.getNativeTitle())
                .pages(entity.getPages().stream().map(this::toDto).toList())
                .build();
    }

    /* ***************** */
    /* ** Page Mapper ** */
    /* ***************** */

    public Page toEntity(PageDTO dto) {
        if (dto == null) return null;
        return Page.builder()
                .pageNumber(dto.pageNumber())
                .lesson(toEntity(dto.lesson()))
                .build();
    }

    public PageDTO toDto(Page entity) {
        if (entity == null) return null;
        return PageDTO.builder()
                .id(entity.getId())
                .pageNumber(entity.getPageNumber())
                .lesson(toDto(entity.getLesson()))
                .build();
    }

    /* ******************* */
    /* ** Lesson Mapper ** */
    /* ******************* */

    // Main dispatcher methods
    public Lesson toEntity(LessonDTO dto) {
        return switch (dto) {
            case null -> null;
            case VocabularyLessonDTO vocabDto -> toEntity(vocabDto);
            case PracticeLessonDTO practiceDto -> toEntity(practiceDto);
            case GrammarLessonDTO grammarDto -> toEntity(grammarDto);
            case ConjugationLessonDTO conjugationDto -> toEntity(conjugationDto);
            case ReadingComprehensionLessonDTO readingDto -> toEntity(readingDto);
            default -> throw new IllegalArgumentException("Unknown lesson DTO type: " + dto.getClass().getSimpleName());
        };
    }

    public LessonDTO toDto(Lesson entity) {
        return switch (entity) {
            case null -> null;
            case VocabularyLesson vocabEntity -> toDto(vocabEntity);
            case PracticeLesson practiceEntity -> toDto(practiceEntity);
            case GrammarLesson grammarEntity -> toDto(grammarEntity);
            case ConjugationLesson conjugationEntity -> toDto(conjugationEntity);
            case ReadingComprehensionLesson readingEntity -> toDto(readingEntity);
            default ->
                    throw new IllegalArgumentException("Unknown lesson entity type: " + entity.getClass().getSimpleName());
        };
    }

    // Specific lesson type mappers
    private VocabularyLesson toEntity(VocabularyLessonDTO dto) {
        if (dto == null) return null;
        return VocabularyLesson.builder()
                .title(dto.title())
                .type(LessonType.VOCABULARY)
                .vocabularies(dto.vocabularies().stream()
                        .map(this::toEntity)
                        .filter(Objects::nonNull)
                        .toList())
                .build();
    }

    private VocabularyLessonDTO toDto(VocabularyLesson entity) {
        if (entity == null) return null;
        return VocabularyLessonDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .vocabularies(entity.getVocabularies().stream()
                        .map(this::toDto)
                        .toList())
                .build();
    }

    private PracticeLesson toEntity(PracticeLessonDTO dto) {
        if (dto == null) return null;
        PracticeLesson lesson = PracticeLesson.builder()
                .title(dto.title())
                .type(LessonType.PRACTICE)
                .instructions(dto.instructions())
                .build();

        if (dto.questions() != null) {
            dto.questions().stream()
                    .map(qDto -> toEntity(qDto, lesson))
                    .forEach(lesson::addQuestion);
        }
        return lesson;
    }

    private PracticeLessonDTO toDto(PracticeLesson entity) {
        if (entity == null) return null;
        return PracticeLessonDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .instructions(entity.getInstructions())
                .questions(entity.getQuestions().stream()
                        .map(this::toDto)
                        .toList())
                .build();
    }

    private GrammarLesson toEntity(GrammarLessonDTO dto) {
        if (dto == null) return null;
        GrammarLesson lesson = GrammarLesson.builder()
                .title(dto.title())
                .type(LessonType.GRAMMAR)
                .grammarConcept(dto.grammarConcept())
                .nativeGrammarConcept(dto.nativeGrammarConcept())
                .explanation(dto.explanation())
                .build();

        if (dto.exampleSentences() != null) {
            dto.exampleSentences().stream()
                    .map(this::toEntity)
                    .forEach(lesson::addExampleSentence);
        }
        return lesson;
    }

    private GrammarLessonDTO toDto(GrammarLesson entity) {
        if (entity == null) return null;
        return GrammarLessonDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .grammarConcept(entity.getGrammarConcept())
                .nativeGrammarConcept(entity.getNativeGrammarConcept())
                .explanation(entity.getExplanation())
                .exampleSentences(entity.getExampleSentences().stream()
                        .map(this::toDto)
                        .toList())
                .build();
    }

    private ConjugationLesson toEntity(ConjugationLessonDTO dto) {
        if (dto == null) return null;
        ConjugationLesson lesson = ConjugationLesson.builder()
                .title(dto.title())
                .type(LessonType.CONJUGATION)
                .conjugationRuleName(dto.conjugationRuleName())
                .explanation(dto.explanation())
                .build();

        if (dto.conjugatedWords() != null) {
            dto.conjugatedWords().stream()
                    .map(exampleDto -> toEntity(exampleDto, lesson))
                    .forEach(lesson::addConjugationExample);
        }
        return lesson;
    }

    private ConjugationLessonDTO toDto(ConjugationLesson entity) {
        if (entity == null) return null;
        return ConjugationLessonDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .conjugationRuleName(entity.getConjugationRuleName())
                .explanation(entity.getExplanation())
                .conjugatedWords(entity.getConjugatedWords().stream()
                        .map(this::toDto)
                        .toList())
                .build();
    }

    private ReadingComprehensionLesson toEntity(ReadingComprehensionLessonDTO dto) {
        if (dto == null) return null;
        ReadingComprehensionLesson lesson = ReadingComprehensionLesson.builder()
                .title(dto.title())
                .type(LessonType.READING_COMPREHENSION)
                .story(dto.story())
                .build();

        if (dto.questions() != null) {
            dto.questions().stream()
                    .map(qDto -> toEntity(qDto, lesson))
                    .forEach(lesson::addQuestion);
        }
        return lesson;
    }

    private ReadingComprehensionLessonDTO toDto(ReadingComprehensionLesson entity) {
        if (entity == null) return null;
        return ReadingComprehensionLessonDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .story(entity.getStory())
                .questions(entity.getQuestions().stream()
                        .map(this::toDto)
                        .toList())
                .build();
    }

    /* ******************* */
    /* **  Word Mapper  ** */
    /* ******************* */

    public Word toEntity(WordDTO dto) {
        if (dto == null) return null;
        return Word.builder()
                .englishTranslation(dto.englishTranslation())
                .nativeWord(dto.nativeWord())
                .language(dto.language())
                .phoneticSpelling(dto.phoneticSpelling())
                .details(dto.details())
                .build();
    }

    public WordDTO toDto(Word entity) {
        if (entity == null) return null;
        return WordDTO.builder()
                .id(entity.getId())
                .englishTranslation(entity.getEnglishTranslation())
                .language(entity.getLanguage())
                .nativeWord(entity.getNativeWord())
                .phoneticSpelling(entity.getPhoneticSpelling())
                .details(entity.getDetails())
                .build();
    }

    /* ********************* */
    /* ** Sentence Mapper ** */
    /* ********************* */

    public Sentence toEntity(SentenceDTO dto) {
        if (dto == null) return null;
        return Sentence.builder()
                .translation(dto.translation())
                .text(dto.text())
                .build();
    }

    public SentenceDTO toDto(Sentence entity) {
        if (entity == null) return null;
        return SentenceDTO.builder()
                .id(entity.getId())
                .translation(entity.getTranslation())
                .text(entity.getText())
                .build();
    }

    /* *********************************** */
    /* **  Conjugation Example Mapper   ** */
    /* *********************************** */

    public ConjugationExample toEntity(ConjugationExampleDTO dto, ConjugationLesson lesson) {
        if (dto == null) return null;
        return ConjugationExample.builder()
                .conjugatedForm(dto.conjugatedForm())
                .infinitive(dto.infinitive())
                .exampleSentence(dto.exampleSentence())
                .sentenceTranslation(dto.sentenceTranslation())
                .lesson(lesson)
                .build();
    }

    public ConjugationExampleDTO toDto(ConjugationExample entity) {
        if(entity == null) return null;
        return ConjugationExampleDTO.builder()
                .id(entity.getId())
                .conjugatedForm(entity.getConjugatedForm())
                .infinitive(entity.getInfinitive())
                .exampleSentence(entity.getExampleSentence())
                .sentenceTranslation(entity.getSentenceTranslation())
                .build();
    }

    /* ************************ */
    /* **  Question Mapper   ** */
    /* ************************ */

    public Question toEntity(QuestionDTO dto, Lesson lesson) {
        if (dto == null) return null;
        return Question.builder()
                .questionType(QuestionType.valueOf(dto.questionType()))
                .questionText(dto.questionText())
                .answer(dto.answer())
                .options(dto.options())
                .lesson(lesson)
                .build();
    }

    public QuestionDTO toDto(Question entity) {
        if (entity == null) return null;
        return QuestionDTO.builder()
                .id(entity.getId())
                .questionType(entity.getQuestionType().name())
                .questionText(entity.getQuestionText())
                .answer(entity.getAnswer())
                .options(entity.getOptions())
                .build();
    }

    /* **************************** */
    /* **      User Mapper       ** */
    /* **************************** */

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = User.builder()
                .username(dto.username())
                .settings(toEntity(dto.settings()))
                .build();

        if(dto.lessonBookList() != null) {
            dto.lessonBookList().stream().map(this::toEntity).forEach(user::addLessonBook);
        }
        return user;
    }

    public UserDTO toDto(User entity) {
        if (entity == null) return null;
        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .settings(toDto(entity.getSettings()))
                .lessonBookList(entity.getLessonBookList().stream().map(this::toDto).toList())
                .build();
    }

    /* **************************** */
    /* **    Settings Mapper     ** */
    /* **************************** */

    public Settings toEntity(SettingsDTO dto) {
        return Settings.builder()
                .language(dto.language())
                .difficulty(dto.difficulty())
                .build();
    }

    public SettingsDTO toDto(Settings entity) {
        return SettingsDTO.builder()
                .id(entity.getId())
                .language(entity.getLanguage())
                .difficulty(entity.getDifficulty())
                .build();
    }

}
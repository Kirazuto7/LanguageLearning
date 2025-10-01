package com.example.language_learning.shared.mapper;

import com.example.language_learning.lessonbook.LessonBookStructMapper;
import com.example.language_learning.lessonbook.chapter.LessonChapterStructMapper;
import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.chapter.lesson.mappers.*;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageStructMapper;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestionStructMapper;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.*;
import com.example.language_learning.shared.word.mappers.WordDetailsStructMapper;
import com.example.language_learning.shared.word.mappers.WordStructMapper;
import com.example.language_learning.shared.word.mappers.*;
import com.example.language_learning.storybook.StoryBook;
import com.example.language_learning.storybook.StoryBookDTO;
import com.example.language_learning.storybook.StoryBookStructMapper;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.ShortStoryDTO;
import com.example.language_learning.storybook.shortstory.ShortStoryStructMapper;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageStructMapper;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraphDTO;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraphStructMapper;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItem;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItemDTO;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItemStructMapper;
import com.example.language_learning.user.*;
import com.example.language_learning.shared.word.dtos.WordDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestionDTO;
import com.example.language_learning.shared.word.dtos.WordDetailsDTO;
import com.example.language_learning.shared.word.data.Word;
import com.example.language_learning.lessonbook.chapter.LessonChapterDTO;
import com.example.language_learning.lessonbook.chapter.lesson.data.ConjugationLesson;
import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.lessonbook.chapter.lesson.dtos.LessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPage;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestion;
import com.example.language_learning.shared.word.data.WordDetails;
import com.example.language_learning.lessonbook.LessonBook;
import com.example.language_learning.lessonbook.LessonBookDTO;
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
    private final LessonChapterStructMapper lessonChapterStructMapper;
    private final LessonPageStructMapper lessonPageStructMapper;
    private final LessonStructMapper lessonStructMapper;
    private final VocabularyLessonStructMapper vocabularyLessonStructMapper;
    private final GrammarLessonStructMapper grammarLessonStructMapper;
    private final ConjugationLessonStructMapper conjugationLessonStructMapper;
    private final PracticeLessonStructMapper practiceLessonStructMapper;
    private final ReadingComprehensionLessonStructMapper readingComprehensionLessonStructMapper;
    private final WordStructMapper wordStructMapper;
    private final WordDetailsStructMapper wordDetailsStructMapper;
    private final LessonSentenceStructMapper lessonSentenceStructMapper;
    private final LessonQuestionStructMapper lessonQuestionStructMapper;
    private final LessonConjugationExampleStructMapper lessonConjugationExampleStructMapper;
    private final KoreanWordDetailsStructMapper koreanWordDetailsStructMapper;
    private final ChineseWordDetailsStructMapper chineseWordDetailsStructMapper;
    private final JapaneseWordDetailsStructMapper japaneseWordDetailsStructMapper;
    private final ThaiWordDetailsStructMapper thaiWordDetailsStructMapper;
    private final ItalianWordDetailsStructMapper italianWordDetailsStructMapper;
    private final SpanishWordDetailsStructMapper spanishWordDetailsStructMapper;
    private final FrenchWordDetailsStructMapper frenchWordDetailsStructMapper;
    private final GermanWordDetailsStructMapper germanWordDetailsStructMapper;
    private final StoryBookStructMapper storyBookStructMapper;
    private final ShortStoryStructMapper shortStoryStructMapper;
    private final StoryPageStructMapper storyPageStructMapper;
    private final StoryParagraphStructMapper storyParagraphStructMapper;
    private final StoryVocabularyItemStructMapper storyVocabularyItemStructMapper;

    /* ***************** */
    /* ** Book Mapper ** */
    /* ***************** */

    public LessonBook toEntity(LessonBookDTO dto) {
        return lessonBookStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public LessonBookDTO toDto(LessonBook entity) {
        return lessonBookStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ********************** */
    /* ** StoryBook Mapper ** */
    /* ********************** */

    public StoryBook toEntity(StoryBookDTO dto) {
        return storyBookStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public StoryBookDTO toDto(StoryBook entity) {
        return storyBookStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ******************** */
    /* ** Chapter Mapper ** */
    /* ******************** */

    public LessonChapter toEntity(LessonChapterDTO dto) {
        return lessonChapterStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public LessonChapterDTO toDto(LessonChapter entity) {
        return lessonChapterStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ************************ */
    /* ** Short Story Mapper ** */
    /* ************************ */

    public ShortStory toEntity(ShortStoryDTO dto) {
        return shortStoryStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public ShortStoryDTO toDto(ShortStory entity) {
        return shortStoryStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ***************** */
    /* ** Page Mapper ** */
    /* ***************** */

    public LessonPage toEntity(LessonPageDTO dto) {
        return lessonPageStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public LessonPageDTO toDto(LessonPage entity) {
        return lessonPageStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ************************ */
    /* ** Story Page Mapper ** */
    /* ************************ */

    public StoryPage toEntity(StoryPageDTO dto) {
        return storyPageStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public StoryPageDTO toDto(StoryPage entity) {
        StoryPageDTO dto = storyPageStructMapper.toDto(entity, new CycleAvoidingMappingContext());
        // Manually ensure the 'type' field is set, as it's critical for GraphQL's TypeResolver.
        return dto.withType(entity.getType());
    }

    /* **************************** */
    /* ** Story Paragraph Mapper ** */
    /* **************************** */

    public StoryParagraph toEntity(StoryParagraphDTO dto) {
        return storyParagraphStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public StoryParagraphDTO toDto(StoryParagraph entity) {
        return storyParagraphStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ********************************* */
    /* ** Story Vocabulary Item Mapper ** */
    /* ********************************* */

    public StoryVocabularyItem toEntity(StoryVocabularyItemDTO dto) {
        return storyVocabularyItemStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public StoryVocabularyItemDTO toDto(StoryVocabularyItem entity) {
        return storyVocabularyItemStructMapper.toDto(entity, new CycleAvoidingMappingContext());
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

    public LessonSentence toEntity(LessonSentenceDTO dto) {
        return lessonSentenceStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public LessonSentenceDTO toDto(LessonSentence entity) {
        return lessonSentenceStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* *********************************** */
    /* **  Conjugation Example Mapper   ** */
    /* *********************************** */

    public LessonConjugationExample toEntity(LessonConjugationExampleDTO dto, ConjugationLesson lesson) {
        return lessonConjugationExampleStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
    }

    public LessonConjugationExampleDTO toDto(LessonConjugationExample entity) {
        return lessonConjugationExampleStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }

    /* ************************ */
    /* **  Question Mapper   ** */
    /* ************************ */

    public LessonQuestion toEntity(LessonQuestionDTO dto, Lesson lesson) {
        LessonQuestion lessonQuestion = lessonQuestionStructMapper.toEntity(dto, new CycleAvoidingMappingContext());
        if (lessonQuestion != null) {
            lessonQuestion.setLesson(lesson);
        }
        return lessonQuestion;
    }

    public LessonQuestionDTO toDto(LessonQuestion entity) {
        return lessonQuestionStructMapper.toDto(entity, new CycleAvoidingMappingContext());
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

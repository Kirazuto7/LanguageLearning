package com.example.language_learning.mapper;

import com.example.language_learning.dto.SettingsDTO;
import com.example.language_learning.dto.UserDTO;
import com.example.language_learning.dto.languages.JapaneseWordDTO;
import com.example.language_learning.dto.languages.KoreanWordDTO;
import com.example.language_learning.dto.languages.WordDTO;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.*;
import com.example.language_learning.entity.Settings;
import com.example.language_learning.entity.User;
import com.example.language_learning.entity.languages.JapaneseWord;
import com.example.language_learning.entity.languages.KoreanWord;
import com.example.language_learning.entity.languages.Word;
import com.example.language_learning.entity.lessons.*;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.entity.models.Page;
import com.example.language_learning.entity.models.Question;
import com.example.language_learning.entity.models.Sentence;
import com.example.language_learning.entity.models.SentenceWord;
import com.example.language_learning.entity.models.VocabularyWord;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DtoMapper {

    /* ***************** */
    /* ** Book Mapper ** */
    /* ***************** */

    public LessonBook toEntity(LessonBookDTO dto) {
        LessonBook book = new LessonBook();
        book.setId(dto.getId());
        book.setBookTitle(dto.getBookTitle());
        book.setDifficulty(dto.getDifficulty());
        book.setLanguage(dto.getLanguage());

        if (dto.getChapters() != null) {
            // Set the back-reference on each chapter
            dto.getChapters().stream()
                    .map(this::toEntity)
                    .forEach(book::addChapter);
        }
        return book;
    }

    public LessonBookDTO toDto(LessonBook entity) {
        LessonBookDTO dto = new LessonBookDTO();
        dto.setId(entity.getId());
        dto.setBookTitle(entity.getBookTitle());
        dto.setDifficulty(entity.getDifficulty());
        dto.setLanguage(entity.getLanguage());

        if (entity.getChapters() != null) {
            dto.setChapters(entity.getChapters().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }


    /* ******************** */
    /* ** Chapter Mapper ** */
    /* ******************** */

    public Chapter toEntity(ChapterDTO dto) {
        Chapter chapter = new Chapter();
        chapter.setId(dto.getId());
        chapter.setChapterNumber(dto.getChapterNumber());
        chapter.setTitle(dto.getTitle());
        chapter.setNativeTitle(dto.getNativeTitle());
        // The parent LessonBook is set by the parent's mapper or service, not here.
        if (dto.getPages() != null) {
            dto.getPages().stream()
                    .map(this::toEntity)
                    .forEach(chapter::addPage);
        }
        return chapter;
    }

    public ChapterDTO toDto(Chapter entity) {
        ChapterDTO dto = new ChapterDTO();
        dto.setId(entity.getId());
        dto.setChapterNumber(entity.getChapterNumber());
        dto.setTitle(entity.getTitle());
        dto.setNativeTitle(entity.getNativeTitle());
        if (entity.getPages() != null) {
            dto.setPages(entity.getPages().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    /* ***************** */
    /* ** Page Mapper ** */
    /* ***************** */

    public Page toEntity(PageDTO dto) {
        Page page = new Page();
        page.setId(dto.getId());
        page.setPageNumber(dto.getPageNumber());
        if (dto.getLesson() != null) {
            page.setLesson(toEntity(dto.getLesson()));
        }
        return page; 
    }

    public PageDTO toDto(Page entity) {
        PageDTO dto = new PageDTO();
        dto.setId(entity.getId());
        dto.setPageNumber(entity.getPageNumber());
        if (entity.getLesson() != null) {
            dto.setLesson(toDto(entity.getLesson()));
        }
        return dto;
    }

    /* ******************* */
    /* ** Lesson Mapper ** */
    /* ******************* */

    // Main dispatcher methods
    public Lesson toEntity(LessonDTO dto) {
        if (dto instanceof VocabularyLessonDTO vocabDto) return toEntity(vocabDto);
        if (dto instanceof PracticeLessonDTO practiceDto) return toEntity(practiceDto);
        if (dto instanceof GrammarLessonDTO grammarDto) return toEntity(grammarDto);
        if (dto instanceof ReadingComprehensionLessonDTO readingDto) return toEntity(readingDto);
        throw new IllegalArgumentException("Unknown lesson DTO type: " + dto.getClass().getSimpleName());
    }

    public LessonDTO toDto(Lesson entity) {
        if (entity instanceof VocabularyLesson vocabEntity) return toDto(vocabEntity);
        if (entity instanceof PracticeLesson practiceEntity) return toDto(practiceEntity);
        if (entity instanceof GrammarLesson grammarEntity) return toDto(grammarEntity);
        if (entity instanceof ReadingComprehensionLesson readingEntity) return toDto(readingEntity);
        throw new IllegalArgumentException("Unknown lesson entity type: " + entity.getClass().getSimpleName());
    }

    // Private helper to map base properties
    private void mapLessonBaseProperties(Lesson lesson, LessonDTO dto) {
        lesson.setId(dto.getId());
        lesson.setTitle(dto.getTitle());
        lesson.setType(dto.getType());
        // The parent Page is set by the parent's mapper or service, not here.
    }

    private void mapLessonBaseProperties(LessonDTO dto, Lesson lesson) {
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setType(lesson.getType());
        // Do not map the parent Page to avoid circular dependencies.
    }

    // Specific lesson type mappers
    private VocabularyLesson toEntity(VocabularyLessonDTO dto) {
        VocabularyLesson lesson = new VocabularyLesson();
        mapLessonBaseProperties(lesson, dto);
        if (dto.getVocabularies() != null) {
            dto.getVocabularies().stream()
                    .map(this::toEntity)
                    .forEach(lesson::addVocabulary);
        }
        return lesson;
    }

    private VocabularyLessonDTO toDto(VocabularyLesson entity) {
        VocabularyLessonDTO dto = new VocabularyLessonDTO();
        mapLessonBaseProperties(dto, entity);
        dto.setVocabularies(entity.getVocabularies().stream().map(this::toDto).collect(Collectors.toList()));
        return dto;
    }

    private PracticeLesson toEntity(PracticeLessonDTO dto) {
        PracticeLesson lesson = new PracticeLesson();
        mapLessonBaseProperties(lesson, dto);
        lesson.setInstructions(dto.getInstructions());
        if (dto.getQuestions() != null) {
            dto.getQuestions().stream()
                    .map(this::toEntity) // This will call toEntity(QuestionDTO)
                    .forEach(lesson::addQuestion);
        }
        return lesson;
    }

    private PracticeLessonDTO toDto(PracticeLesson entity) {
        PracticeLessonDTO dto = new PracticeLessonDTO();
        mapLessonBaseProperties(dto, entity);
        dto.setInstructions(entity.getInstructions());
        if (entity.getQuestions() != null) {
            dto.setQuestions(entity.getQuestions().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    private GrammarLesson toEntity(GrammarLessonDTO dto) {
        GrammarLesson lesson = new GrammarLesson();
        mapLessonBaseProperties(lesson, dto);
        lesson.setGrammarConcept(dto.getGrammarConcept());
        lesson.setExplanation(dto.getExplanation());
        if (dto.getExampleSentences() != null) {
            lesson.setExamples(dto.getExampleSentences().stream().map(this::toEntity).collect(Collectors.toList()));
        }
        return lesson;
    }

    private GrammarLessonDTO toDto(GrammarLesson entity) {
        GrammarLessonDTO dto = new GrammarLessonDTO();
        mapLessonBaseProperties(dto, entity);
        dto.setGrammarConcept(entity.getGrammarConcept());
        dto.setExplanation(entity.getExplanation());
        dto.setExampleSentences(entity.getExamples().stream().map(this::toDto).collect(Collectors.toList()));
        return dto;
    }

    private ReadingComprehensionLesson toEntity(ReadingComprehensionLessonDTO dto) {
        ReadingComprehensionLesson lesson = new ReadingComprehensionLesson();
        mapLessonBaseProperties(lesson, dto);
        lesson.setStory(dto.getStory());
        if (dto.getQuestions() != null) {
            dto.getQuestions().stream()
                    .map(this::toEntity) // This will call toEntity(QuestionDTO)
                    .forEach(lesson::addQuestion);
        }
        return lesson;
    }

    private ReadingComprehensionLessonDTO toDto(ReadingComprehensionLesson entity) {
        ReadingComprehensionLessonDTO dto = new ReadingComprehensionLessonDTO();
        mapLessonBaseProperties(dto, entity);
        dto.setStory(entity.getStory());
        if (entity.getQuestions() != null) {
            dto.setQuestions(entity.getQuestions().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    /* ******************* */
    /* **  Word Mapper  ** */
    /* ******************* */

    public Word toEntity(WordDTO dto) {
        if (dto instanceof KoreanWordDTO koreanDto) {
            KoreanWord word = new KoreanWord();
            word.setId(koreanDto.getId());
            word.setHangeul(koreanDto.getHangeul());
            word.setTranslation(koreanDto.getTranslation());
            word.setHanja(koreanDto.getHanja());
            return word;
        }
        else if (dto instanceof JapaneseWordDTO japaneseDto) {
            JapaneseWord word = new JapaneseWord();
            word.setId(japaneseDto.getId());
            word.setTranslation(japaneseDto.getTranslation());
            word.setHiragana(japaneseDto.getHiragana());
            word.setKatakana(japaneseDto.getKatakana());
            word.setKanji(japaneseDto.getKanji());
            word.setRomaji(japaneseDto.getRomaji());
            return word;
        }
        throw new IllegalArgumentException("Unknown Word DTO type: " + dto.getClass().getSimpleName());
    }

    public WordDTO toDto(Word entity) {
        if (entity instanceof KoreanWord koreanEntity) {
            KoreanWordDTO dto = new KoreanWordDTO();
            dto.setId(koreanEntity.getId());
            dto.setHangeul(koreanEntity.getHangeul());
            dto.setHanja(koreanEntity.getHanja());
            dto.setTranslation(koreanEntity.getTranslation());
            return dto;
        }
        else if (entity instanceof JapaneseWord japaneseEntity) {
            JapaneseWordDTO dto = new JapaneseWordDTO();
            dto.setId(japaneseEntity.getId());
            dto.setTranslation(japaneseEntity.getTranslation());
            dto.setHiragana(japaneseEntity.getHiragana());
            dto.setKatakana(japaneseEntity.getKatakana());
            dto.setKanji(japaneseEntity.getKanji());
            dto.setRomaji(japaneseEntity.getRomaji());
            return dto;
        }
        throw new IllegalArgumentException("Unknown Word entity type: " + entity.getClass().getSimpleName());
    }

    /* **************************** */
    /* ** Vocabulary Word Mapper ** */
    /* **************************** */

    public VocabularyWord toEntity(VocabularyWordDTO dto) {
        VocabularyWord item = new VocabularyWord();
        item.setId(dto.getId());
        // The Word entity should be fetched from the DB in the service layer, not created here.
        item.setWord(toEntity(dto.getWord()));
        // The parent Lesson is set by the parent's mapper or service, not here.
        item.setWordIndex(dto.getWordIndex());
        return item;
    }

    public VocabularyWordDTO toDto(VocabularyWord entity) {
        VocabularyWordDTO dto = new VocabularyWordDTO();
        dto.setId(entity.getId());
        dto.setWord(toDto(entity.getWord()));
        // Do not map the parent Lesson to avoid circular dependencies.
        dto.setWordIndex(entity.getWordIndex());
        return dto;
    }

    /* ************************** */
    /* ** Sentence Word Mapper ** */
    /* ************************** */

    public SentenceWord toEntity(SentenceWordDTO dto) {
        SentenceWord entity = new SentenceWord();
        entity.setId(dto.getId());
        entity.setWordIndex(dto.getWordIndex());
        // The Word entity should be fetched from the DB in the service layer, not created here.
        entity.setWord(toEntity(dto.getWord()));
        // The parent Sentence is set by the parent's mapper or service, not here.
        return entity;
    }

    public SentenceWordDTO toDto(SentenceWord entity) {
        SentenceWordDTO dto = new SentenceWordDTO();
        dto.setId(entity.getId());
        dto.setWordIndex(entity.getWordIndex());
        dto.setWord(toDto(entity.getWord()));
        // Do not map the parent Sentence to avoid circular dependencies.
        return dto;
    }

    /* ********************* */
    /* ** Sentence Mapper ** */
    /* ********************* */

    public Sentence toEntity(SentenceDTO dto) {
        Sentence sentence = new Sentence();
        sentence.setId(dto.getId());
        sentence.setTranslation(dto.getTranslation());
        sentence.setText(dto.getText());
        if(dto.getWords() != null) {
            dto.getWords().stream()
                    .map(this::toEntity)
                    .forEach(sentence::addWord);
        }
        return sentence;
    }

    public SentenceDTO toDto(Sentence entity) {
        SentenceDTO dto = new SentenceDTO();
        dto.setId(entity.getId());
        dto.setTranslation(entity.getTranslation());
        dto.setText(entity.getText());
        if(entity.getWords() != null) {
            // Map from List<SentenceWord> to a cleaner List<WordDTO> for the client
            dto.setWords(entity.getWords().stream()
                    .map(SentenceWord::getWord) // Extract the Word from the join entity
                    .map(this::toDto)           // Map the Word to a WordDTO
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    /* ************************ */
    /* **  Question Mapper   ** */
    /* ************************ */

    public Question toEntity(QuestionDTO dto) {
        if (dto == null) return null;
        Question entity = new Question(dto.getQuestionType(), dto.getQuestionText());
        entity.setAnswer(dto.getAnswer());
        entity.setOptions(dto.getOptions());
        // The parent Lesson is set by the parent's mapper or service, not here.
        return entity;
    }

    public QuestionDTO toDto(Question entity) {
        if (entity == null) return null;
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionType(entity.getQuestionType());
        dto.setQuestionText(entity.getQuestionText());
        dto.setAnswer(entity.getAnswer());
        dto.setOptions(entity.getOptions());
        return dto;
    }

    /* **************************** */
    /* **      User Mapper       ** */
    /* **************************** */

    public User toEntity(UserDTO dto) {
        User entity = new User();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        if (dto.getSettings() != null) {
            entity.setSettings(toEntity(dto.getSettings()));
        }
        if(dto.getLessonBookList() != null) {
            dto.getLessonBookList().stream().map(this::toEntity).forEach(entity::addLessonBook);
        }
        return entity;
    }

    public UserDTO toDto(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        if (entity.getSettings() != null) {
            dto.setSettings(toDto(entity.getSettings()));
        }
        if(entity.getLessonBookList() != null) {
            dto.setLessonBookList(entity.getLessonBookList().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    /* **************************** */
    /* **    Settings Mapper     ** */
    /* **************************** */

    public Settings toEntity(SettingsDTO dto) {
        Settings entity = new Settings();
        entity.setId(dto.getId());
        entity.setLanguage(dto.getLanguage());
        entity.setDifficulty(dto.getDifficulty());
        return entity;
    }

    public SettingsDTO toDto(Settings entity) {
       SettingsDTO dto = new SettingsDTO();
       dto.setId(entity.getId());
       dto.setLanguage(entity.getLanguage());
       dto.setDifficulty(entity.getDifficulty());
       return dto;
    }

}
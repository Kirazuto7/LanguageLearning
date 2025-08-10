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
import com.example.language_learning.entity.models.*;
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
        book.setUser(toEntity(dto.getUser()));
        if (dto.getChapters() != null) {
            book.setChapters(dto.getChapters().stream().map(this::toEntity).collect(Collectors.toList()));
        }
        return book;
    }

    public LessonBookDTO toDto(LessonBook entity) {
        LessonBookDTO dto = new LessonBookDTO();
        dto.setId(entity.getId());
        dto.setBookTitle(entity.getBookTitle());
        dto.setDifficulty(entity.getDifficulty());
        dto.setLanguage(entity.getLanguage());
        dto.setUser(toDto(entity.getUser()));
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
        chapter.setLessonBook(toEntity(dto.getLessonBook()));
        if (dto.getPages() != null) {
            chapter.setPages(dto.getPages().stream().map(this::toEntity).collect(Collectors.toList()));
        }
        return chapter;
    }

    public ChapterDTO toDto(Chapter entity) {
        ChapterDTO dto = new ChapterDTO();
        dto.setId(entity.getId());
        dto.setChapterNumber(entity.getChapterNumber());
        dto.setTitle(entity.getTitle());
        dto.setNativeTitle(entity.getNativeTitle());
        dto.setLessonBook(toDto(entity.getLessonBook()));
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
        page.setChapter(toEntity(dto.getChapter()));
        if (dto.getLesson() != null) {
            page.setLesson(toEntity(dto.getLesson()));
        }
        return page; 
    }

    public PageDTO toDto(Page entity) {
        PageDTO dto = new PageDTO();
        dto.setId(entity.getId());
        dto.setPageNumber(entity.getPageNumber());
        dto.setChapter(toDto(entity.getChapter()));
        if (entity.getLesson() != null) {
            dto.setLesson(toDto(entity.getLesson()));
        }
        return dto;
    }

    /* ******************* */
    /* ** Lesson Mapper ** */
    /* ******************* */

    public Lesson toEntity(LessonDTO dto) {
        if (dto instanceof VocabularyLessonDTO vocabDto) {
            VocabularyLesson lesson = new VocabularyLesson();
            lesson.setId(vocabDto.getId());
            lesson.setTitle(vocabDto.getTitle());
            lesson.setType(vocabDto.getType());
            if(vocabDto.getPage() != null)
                lesson.setPage(toEntity(vocabDto.getPage()));
            if (vocabDto.getVocabularies() != null) {
                lesson.setVocabularies(vocabDto.getVocabularies().stream().map(this::toEntity).collect(Collectors.toList()));
            }
            return lesson;
        }
        else if(dto instanceof SentenceLessonDTO sentenceLessonDTO) {
            SentenceLesson lesson = new SentenceLesson();
            lesson.setId(sentenceLessonDTO.getId());
            lesson.setTitle(sentenceLessonDTO.getTitle());
            lesson.setType(sentenceLessonDTO.getType());
            if(sentenceLessonDTO.getPage() != null)
                lesson.setPage(toEntity(sentenceLessonDTO.getPage()));
            if (sentenceLessonDTO.getSentences() != null) {
                lesson.setSentences(sentenceLessonDTO.getSentences().stream().map(this::toEntity).collect(Collectors.toList()));
            }
            return lesson;
        }
        else if(dto instanceof GrammarLessonDTO grammarLessonDTO) {
            GrammarLesson lesson = new GrammarLesson();
            lesson.setId(grammarLessonDTO.getId());
            lesson.setTitle(grammarLessonDTO.getTitle());
            lesson.setType(grammarLessonDTO.getType());
            lesson.setGrammarConcept(grammarLessonDTO.getGrammarConcept());
            lesson.setExplanation(grammarLessonDTO.getExplanation());
            if(grammarLessonDTO.getPage() != null)
                lesson.setPage(toEntity(grammarLessonDTO.getPage()));
            if(grammarLessonDTO.getExampleSentences() != null) {
                lesson.setExamples(grammarLessonDTO.getExampleSentences().stream().map(this::toEntity).collect(Collectors.toList()));
            }
            return lesson;
        }
        else if(dto instanceof ReadingComprehensionLessonDTO readingComprehensionLessonDTO) {
            ReadingComprehensionLesson lesson = new ReadingComprehensionLesson();
            lesson.setId(readingComprehensionLessonDTO.getId());
            lesson.setTitle(readingComprehensionLessonDTO.getTitle());
            lesson.setType(readingComprehensionLessonDTO.getType());
            lesson.setStory(readingComprehensionLessonDTO.getStory());
            if(readingComprehensionLessonDTO.getPage() != null)
                lesson.setPage(toEntity(readingComprehensionLessonDTO.getPage()));
            if(readingComprehensionLessonDTO.getQuestions() != null) {
                lesson.setQuestions(readingComprehensionLessonDTO.getQuestions().stream().map(this::toEntity).collect(Collectors.toList()));
            }
            return lesson;
        }
        // Add other lesson types here in the future
        throw new IllegalArgumentException("Unknown lesson DTO type: " + dto.getClass().getSimpleName());
    }

    public LessonDTO toDto(Lesson entity) {
        if (entity instanceof VocabularyLesson vocabEntity) {
            VocabularyLessonDTO dto = new VocabularyLessonDTO();
            dto.setId(vocabEntity.getId());
            dto.setTitle(vocabEntity.getTitle());
            dto.setType(vocabEntity.getType());
            if(vocabEntity.getPage() != null)
                dto.setPage(toDto(vocabEntity.getPage()));
            dto.setVocabularies(vocabEntity.getVocabularies().stream().map(this::toDto).collect(Collectors.toList()));
            return dto;
        }
        else if(entity instanceof SentenceLesson sentenceEntity) {
            SentenceLessonDTO dto = new SentenceLessonDTO();
            dto.setId(sentenceEntity.getId());
            dto.setTitle(sentenceEntity.getTitle());
            dto.setType(sentenceEntity.getType());
            if(sentenceEntity.getPage() != null)
                dto.setPage(toDto(sentenceEntity.getPage()));
            dto.setSentences(sentenceEntity.getSentences().stream().map(this::toDto).collect(Collectors.toList()));
            return dto;
        }
        else if(entity instanceof GrammarLesson grammarEntity) {
            GrammarLessonDTO dto = new GrammarLessonDTO();
            dto.setId(grammarEntity.getId());
            dto.setTitle(grammarEntity.getTitle());
            dto.setType(grammarEntity.getType());
            dto.setGrammarConcept(grammarEntity.getGrammarConcept());
            dto.setExplanation(grammarEntity.getExplanation());
            if(grammarEntity.getPage() != null)
                dto.setPage(toDto(grammarEntity.getPage()));
            if(grammarEntity.getExamples() != null) {
                dto.setExampleSentences(grammarEntity.getExamples().stream().map(this::toDto).collect(Collectors.toList()));
            }
            return dto;
        }
        else if(entity instanceof ReadingComprehensionLesson readingComprehensionLesson) {
            ReadingComprehensionLessonDTO dto = new ReadingComprehensionLessonDTO();
            dto.setId(readingComprehensionLesson.getId());
            dto.setTitle(readingComprehensionLesson.getTitle());
            dto.setType(readingComprehensionLesson.getType());
            dto.setStory(readingComprehensionLesson.getStory());
            if(readingComprehensionLesson.getPage() != null)
                dto.setPage(toDto(readingComprehensionLesson.getPage()));
            if(readingComprehensionLesson.getQuestions() != null) {
                dto.setQuestions(readingComprehensionLesson.getQuestions().stream().map(this::toDto).collect(Collectors.toList()));
            }
            return dto;
        }
        throw new IllegalArgumentException("Unknown lesson entity type: " + entity.getClass().getSimpleName());
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
        throw new IllegalArgumentException("Unknown lesson entity type: " + dto.getClass().getSimpleName());
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
        throw new IllegalArgumentException("Unknown lesson entity type: " + entity.getClass().getSimpleName());
    }

    /* **************************** */
    /* ** Vocabulary Word Mapper ** */
    /* **************************** */

    public VocabularyWord toEntity(VocabularyWordDTO dto) {
        VocabularyWord item = new VocabularyWord();
        item.setId(dto.getId());
        item.setWord(toEntity(dto.getWord()));
        item.setLesson((VocabularyLesson) toEntity(dto.getLesson()));
        item.setWordIndex(dto.getWordIndex());
        return item;
    }

    public VocabularyWordDTO toDto(VocabularyWord entity) {
        VocabularyWordDTO dto = new VocabularyWordDTO();
        dto.setId(entity.getId());
        dto.setWord(toDto(entity.getWord()));
        dto.setLesson((VocabularyLessonDTO) toDto(entity.getLesson()));
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
        entity.setWord(toEntity(dto.getWord()));
        entity.setSentence(toEntity(dto.getSentence()));
        return entity;
    }

    public SentenceWordDTO toDto(SentenceWord entity) {
        SentenceWordDTO dto = new SentenceWordDTO();
        dto.setId(entity.getId());
        dto.setWordIndex(entity.getWordIndex());
        dto.setWord(toDto(entity.getWord()));
        dto.setSentence(toDto(entity.getSentence()));
        return dto;
    }

    /* ********************* */
    /* ** Sentence Mapper ** */
    /* ********************* */

    public Sentence toEntity(SentenceDTO dto) {
        Sentence sentence = new Sentence();
        sentence.setId(dto.getId());
        sentence.setTranslation(dto.getTranslation());
        if(dto.getWords() != null) {
            sentence.setWords(dto.getWords().stream().map(this::toEntity).collect(Collectors.toList()));
        }
        return sentence;
    }

    public SentenceDTO toDto(Sentence entity) {
        SentenceDTO dto = new SentenceDTO();
        dto.setId(entity.getId());
        dto.setTranslation(entity.getTranslation());
        if(entity.getWords() != null) {
            dto.setWords(entity.getWords().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    /* **************************** */
    /* ** Multiple Choice Mapper ** */
    /* **************************** */

    public MultipleChoice toEntity(MultipleChoiceDTO dto) {
        MultipleChoice entity = new MultipleChoice();
        entity.setId(dto.getId());
        entity.setQuestion(dto.getQuestion());
        entity.setAnswer(dto.getAnswer());
        entity.setAnswerChoices(dto.getAnswerChoices());
        return entity;
    }

    public MultipleChoiceDTO toDto(MultipleChoice entity) {
        MultipleChoiceDTO dto = new MultipleChoiceDTO();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setAnswer(entity.getAnswer());
        dto.setAnswerChoices(entity.getAnswerChoices());
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
        entity.setSettings(toEntity(dto.getSettings()));
        if(dto.getLessonBookList() != null) {
            entity.setLessonBookList(dto.getLessonBookList().stream().map(this::toEntity).collect(Collectors.toList()));
        }
        return entity;
    }

    public UserDTO toDto(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setSettings(toDto(entity.getSettings()));
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
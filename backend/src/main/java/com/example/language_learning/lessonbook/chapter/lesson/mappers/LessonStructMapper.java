package com.example.language_learning.lessonbook.chapter.lesson.mappers;

import com.example.language_learning.lessonbook.chapter.lesson.page.question.LessonQuestionStructMapper;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonConjugationExampleStructMapper;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonSentenceStructMapper;
import com.example.language_learning.shared.enums.LessonType;
import com.example.language_learning.lessonbook.chapter.lesson.data.*;
import com.example.language_learning.lessonbook.chapter.lesson.dtos.*;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.shared.word.mappers.WordStructMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {
        VocabularyLessonStructMapper.class,
        GrammarLessonStructMapper.class,
        ConjugationLessonStructMapper.class,
        PracticeLessonStructMapper.class,
        ReadingComprehensionLessonStructMapper.class
    },
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class LessonStructMapper {

    protected WordStructMapper wordStructMapper;
    protected LessonSentenceStructMapper lessonSentenceStructMapper;
    protected LessonQuestionStructMapper lessonQuestionStructMapper;
    protected LessonConjugationExampleStructMapper lessonConjugationExampleStructMapper;

    @Autowired
    public void setWordStructMapper(WordStructMapper wordStructMapper) {
        this.wordStructMapper = wordStructMapper;
    }

    @Autowired
    public void setSentenceStructMapper(LessonSentenceStructMapper lessonSentenceStructMapper) {
        this.lessonSentenceStructMapper = lessonSentenceStructMapper;
    }

    @Autowired
    public void setQuestionStructMapper(LessonQuestionStructMapper lessonQuestionStructMapper) {
        this.lessonQuestionStructMapper = lessonQuestionStructMapper;
    }

    @Autowired
    public void setConjugationExampleStructMapper(LessonConjugationExampleStructMapper lessonConjugationExampleStructMapper) {
        this.lessonConjugationExampleStructMapper = lessonConjugationExampleStructMapper;
    }

    @ObjectFactory
    protected Lesson createLesson(LessonDTO dto) {
        return switch (dto) {
            case null -> null;
            case VocabularyLessonDTO v -> VocabularyLesson.builder()
                    .type(LessonType.VOCABULARY)
                    .title(v.title())
                    .build();
            case GrammarLessonDTO g -> GrammarLesson.builder()
                    .type(LessonType.GRAMMAR)
                    .title(g.title())
                    .grammarConcept(g.grammarConcept())
                    .explanation(g.explanation())
                    .build();
            case ConjugationLessonDTO c -> ConjugationLesson.builder()
                    .type(LessonType.CONJUGATION)
                    .title(c.title())
                    .conjugationRuleName(c.conjugationRuleName())
                    .explanation(c.explanation())
                    .build();
            case PracticeLessonDTO p -> PracticeLesson.builder()
                    .type(LessonType.PRACTICE)
                    .title(p.title())
                    .instructions(p.instructions())
                    .build();
            case ReadingComprehensionLessonDTO r -> ReadingComprehensionLesson.builder()
                    .type(LessonType.READING_COMPREHENSION)
                    .title(r.title())
                    .story(r.story())
                    .build();
            default -> throw new IllegalArgumentException("Unknown DTO type: " + dto.getClass());
        };
    }

    @ObjectFactory
    protected LessonDTO createLessonDTO(Lesson entity, @Context CycleAvoidingMappingContext context) {
        if (entity == null) {
            return null;
        }
        LessonDTO existingDto = context.getMappedInstance(entity, LessonDTO.class);
        if (existingDto != null) {
            return existingDto;
        }

        return switch (entity) {
            case null -> null;
            case VocabularyLesson v -> VocabularyLessonDTO.builder().build();
            case GrammarLesson g -> GrammarLessonDTO.builder().build();
            case ConjugationLesson c -> ConjugationLessonDTO.builder().build();
            case PracticeLesson p -> PracticeLessonDTO.builder().build();
            case ReadingComprehensionLesson r -> ReadingComprehensionLessonDTO.builder().build();
            default -> throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
        };
    }

    @SubclassMapping(source = VocabularyLessonDTO.class, target = VocabularyLesson.class)
    @SubclassMapping(source = GrammarLessonDTO.class, target = GrammarLesson.class)
    @SubclassMapping(source = ConjugationLessonDTO.class, target = ConjugationLesson.class)
    @SubclassMapping(source = PracticeLessonDTO.class, target = PracticeLesson.class)
    @SubclassMapping(source = ReadingComprehensionLessonDTO.class, target = ReadingComprehensionLesson.class)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lessonPage", ignore = true) // Back-reference
    @Mapping(target = "title", ignore = true) // Set in factory
    @Mapping(target = "type", ignore = true) // Set in factory
    @Mapping(target = "createdAt", ignore = true)
    public abstract Lesson toEntity(LessonDTO dto, @Context CycleAvoidingMappingContext context);

    @SubclassMapping(source = VocabularyLesson.class, target = VocabularyLessonDTO.class)
    @SubclassMapping(source = GrammarLesson.class, target = GrammarLessonDTO.class)
    @SubclassMapping(source = ConjugationLesson.class, target = ConjugationLessonDTO.class)
    @SubclassMapping(source = PracticeLesson.class, target = PracticeLessonDTO.class)
    @SubclassMapping(source = ReadingComprehensionLesson.class, target = ReadingComprehensionLessonDTO.class)
    public abstract LessonDTO toDto(Lesson entity, @Context CycleAvoidingMappingContext context);
}

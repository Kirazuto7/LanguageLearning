package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.entity.lessons.*;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(
    componentModel = "spring",
    uses = {
        VocabularyLessonStructMapper.class,
        GrammarLessonStructMapper.class,
        ConjugationLessonStructMapper.class,
        PracticeLessonStructMapper.class,
        ReadingComprehensionStructMapper.class
    },
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface LessonStructMapper {

    @SubclassMapping(source = VocabularyLessonDTO.class, target = VocabularyLesson.class)
    @SubclassMapping(source = GrammarLessonDTO.class, target = GrammarLesson.class)
    @SubclassMapping(source = ConjugationLessonDTO.class, target = ConjugationLesson.class)
    @SubclassMapping(source = PracticeLessonDTO.class, target = PracticeLesson.class)
    @SubclassMapping(source = ReadingComprehensionLessonDTO.class, target = ReadingComprehensionLesson.class)
    Lesson toEntity(LessonDTO dto);

    @SubclassMapping(source = VocabularyLesson.class, target = VocabularyLessonDTO.class)
    @SubclassMapping(source = GrammarLesson.class, target = GrammarLessonDTO.class)
    @SubclassMapping(source = ConjugationLesson.class, target = ConjugationLessonDTO.class)
    @SubclassMapping(source = PracticeLesson.class, target = PracticeLessonDTO.class)
    @SubclassMapping(source = ReadingComprehensionLesson.class, target = ReadingComprehensionLessonDTO.class)
    LessonDTO toDto(Lesson entity);
}

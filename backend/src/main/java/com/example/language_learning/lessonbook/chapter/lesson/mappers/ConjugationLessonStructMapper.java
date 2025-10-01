package com.example.language_learning.lessonbook.chapter.lesson.mappers;

import com.example.language_learning.lessonbook.chapter.lesson.dtos.ConjugationLessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.data.ConjugationLesson;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonConjugationExampleStructMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {LessonConjugationExampleStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class ConjugationLessonStructMapper {
    @Mapping(target = "type", constant = "CONJUGATION")
    @Mapping(target = "lessonPage", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract ConjugationLesson toEntity(ConjugationLessonDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract ConjugationLessonDTO toDto(ConjugationLesson entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ConjugationLessonDTO createDto(ConjugationLesson entity, @Context CycleAvoidingMappingContext context) {
        ConjugationLessonDTO existingDto = context.getMappedInstance(entity, ConjugationLessonDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ConjugationLessonDTO.builder().build();
    }
}

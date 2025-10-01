package com.example.language_learning.lessonbook.chapter.lesson.mappers;

import com.example.language_learning.lessonbook.chapter.lesson.dtos.GrammarLessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.data.GrammarLesson;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.LessonSentenceStructMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring",
    uses = {LessonSentenceStructMapper.class},
    collectionMappingStrategy = org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class GrammarLessonStructMapper {
    @Mapping(target = "type", constant = "GRAMMAR")
    @Mapping(target = "lessonPage", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract GrammarLesson toEntity(GrammarLessonDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract GrammarLessonDTO toDto(GrammarLesson entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public GrammarLessonDTO createDto(GrammarLesson entity, @Context CycleAvoidingMappingContext context) {
        GrammarLessonDTO existingDto = context.getMappedInstance(entity, GrammarLessonDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return GrammarLessonDTO.builder().build();
    }
}

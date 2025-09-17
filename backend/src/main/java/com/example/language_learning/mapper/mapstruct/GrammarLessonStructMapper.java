package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.lessons.GrammarLessonDTO;
import com.example.language_learning.entity.lessons.GrammarLesson;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring",
    uses = {SentenceStructMapper.class},
    collectionMappingStrategy = org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class GrammarLessonStructMapper {
    @Mapping(target = "type", constant = "GRAMMAR")
    @Mapping(target = "page", ignore = true)
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

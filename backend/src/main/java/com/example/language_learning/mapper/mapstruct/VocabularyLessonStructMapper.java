package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.entity.lessons.VocabularyLesson;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring",
    uses = {WordStructMapper.class}
)
public abstract class VocabularyLessonStructMapper {
    @Mapping(target = "type", constant = "VOCABULARY")
    @Mapping(target = "page", ignore = true)
    public abstract VocabularyLesson toEntity(VocabularyLessonDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract VocabularyLessonDTO toDto(VocabularyLesson entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public VocabularyLessonDTO createDto(VocabularyLesson entity, @Context CycleAvoidingMappingContext context) {
        VocabularyLessonDTO existingDto = context.getMappedInstance(entity, VocabularyLessonDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return VocabularyLessonDTO.builder().build();
    }
}

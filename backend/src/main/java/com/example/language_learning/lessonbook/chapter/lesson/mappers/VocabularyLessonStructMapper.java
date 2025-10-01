package com.example.language_learning.lessonbook.chapter.lesson.mappers;

import com.example.language_learning.lessonbook.chapter.lesson.dtos.VocabularyLessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.data.VocabularyLesson;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.shared.word.mappers.WordStructMapper;
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
    @Mapping(target = "lessonPage", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
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

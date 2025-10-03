package com.example.language_learning.lessonbook.chapter.lesson.page;

import com.example.language_learning.lessonbook.chapter.lesson.mappers.LessonStructMapper;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.ObjectFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {LessonStructMapper.class}
)
public abstract class LessonPageStructMapper {
    @Mapping(target = "lessonChapter", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract LessonPage toEntity(LessonPageDTO dto, @Context CycleAvoidingMappingContext context);
    public abstract LessonPageDTO toDto(LessonPage entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public LessonPageDTO createDto(LessonPage entity, @Context CycleAvoidingMappingContext context) {
        LessonPageDTO existingDto = context.getMappedInstance(entity, LessonPageDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return LessonPageDTO.builder().build();
    }
}

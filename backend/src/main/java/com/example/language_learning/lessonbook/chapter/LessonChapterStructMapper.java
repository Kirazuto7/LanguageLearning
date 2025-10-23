package com.example.language_learning.lessonbook.chapter;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageStructMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {LessonPageStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class LessonChapterStructMapper {
    @Mapping(target = "lessonBook", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract LessonChapter toEntity(LessonChapterDTO dto, @Context CycleAvoidingMappingContext context);
    public abstract LessonChapterDTO toDto(LessonChapter entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public LessonChapterDTO createDto(LessonChapter entity, @Context CycleAvoidingMappingContext context) {
        LessonChapterDTO existingDto = context.getMappedInstance(entity, LessonChapterDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return LessonChapterDTO.builder().build();
    }
}

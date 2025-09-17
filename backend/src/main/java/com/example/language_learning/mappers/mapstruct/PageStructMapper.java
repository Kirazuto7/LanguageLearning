package com.example.language_learning.mappers.mapstruct;

import com.example.language_learning.mappers.CycleAvoidingMappingContext;
import com.example.language_learning.lessonbook.chapter.lesson.page.dtos.PageDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.data.Page;
import org.mapstruct.Context;
import org.mapstruct.ObjectFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {LessonStructMapper.class}
)
public abstract class PageStructMapper {
    @Mapping(target = "chapter", ignore = true)
    public abstract Page toEntity(PageDTO dto, @Context CycleAvoidingMappingContext context);
    public abstract PageDTO toDto(Page entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public PageDTO createDto(Page entity, @Context CycleAvoidingMappingContext context) {
        PageDTO existingDto = context.getMappedInstance(entity, PageDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return PageDTO.builder().build();
    }
}

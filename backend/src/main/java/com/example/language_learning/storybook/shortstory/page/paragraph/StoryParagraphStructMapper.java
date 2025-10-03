package com.example.language_learning.storybook.shortstory.page.paragraph;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class StoryParagraphStructMapper {

    @Mapping(target = "storyPage", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract StoryParagraph toEntity(StoryParagraphDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract StoryParagraphDTO toDto(StoryParagraph entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public StoryParagraphDTO createDto(StoryParagraph entity, @Context CycleAvoidingMappingContext context) {
        StoryParagraphDTO existingDto = context.getMappedInstance(entity, StoryParagraphDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return StoryParagraphDTO.builder().build();
    }
}

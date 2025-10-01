package com.example.language_learning.storybook.shortstory.page.vocab;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class StoryVocabularyItemStructMapper {

    @Mapping(target = "storyPage", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract StoryVocabularyItem toEntity(StoryVocabularyItemDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract StoryVocabularyItemDTO toDto(StoryVocabularyItem entity, @Context CycleAvoidingMappingContext context);
}

package com.example.language_learning.storybook;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.storybook.shortstory.ShortStoryStructMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {ShortStoryStructMapper.class},
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class StoryBookStructMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract StoryBook toEntity(StoryBookDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract StoryBookDTO toDto(StoryBook storyBook, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public StoryBookDTO createDto(StoryBook storyBook, @Context CycleAvoidingMappingContext context) {
        StoryBookDTO existingDto = context.getMappedInstance(storyBook, StoryBookDTO.class);
        if (existingDto != null) {
            return existingDto;
        }

        return StoryBookDTO.builder().build();
    }
}

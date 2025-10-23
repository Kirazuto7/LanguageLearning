package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.storybook.shortstory.page.StoryPageStructMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {StoryPageStructMapper.class},
        collectionMappingStrategy =  CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class ShortStoryStructMapper {
    @Mapping(target = "storyBook", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract ShortStory toEntity(ShortStoryDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract ShortStoryDTO toDto(ShortStory entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ShortStoryDTO createDto(ShortStory entity, @Context CycleAvoidingMappingContext context) {
        ShortStoryDTO existingDto = context.getMappedInstance(entity, ShortStoryDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ShortStoryDTO.builder().build();
    }
}

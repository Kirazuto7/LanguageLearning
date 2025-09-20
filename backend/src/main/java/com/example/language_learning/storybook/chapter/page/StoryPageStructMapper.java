package com.example.language_learning.storybook.chapter.page;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.shared.word.mappers.WordStructMapper;
import com.example.language_learning.storybook.chapter.page.paragraph.StoryParagraphStructMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {StoryParagraphStructMapper.class, WordStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class StoryPageStructMapper {

    @Mapping(target = "storyChapter", ignore = true)
    public abstract StoryPage toEntity(StoryPageDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract StoryPageDTO toDto(StoryPage entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public StoryPageDTO createDto(StoryPage entity, @Context CycleAvoidingMappingContext context) {
        StoryPageDTO existingDto = context.getMappedInstance(entity, StoryPageDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return StoryPageDTO.builder().build();
    }
}

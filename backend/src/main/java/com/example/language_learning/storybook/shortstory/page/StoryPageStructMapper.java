package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraphStructMapper;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItemStructMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {StoryParagraphStructMapper.class, StoryVocabularyItemStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class StoryPageStructMapper {

    @Mapping(target = "shortStory", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract StoryPage toEntity(StoryPageDTO dto, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "type", target = "type")
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

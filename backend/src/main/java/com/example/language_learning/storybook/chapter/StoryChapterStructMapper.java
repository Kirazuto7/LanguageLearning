package com.example.language_learning.storybook.chapter;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.storybook.chapter.page.StoryPageStructMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {StoryPageStructMapper.class},
        collectionMappingStrategy =  CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class StoryChapterStructMapper {
    @Mapping(target = "storyBook", ignore = true)
    public abstract StoryChapter toEntity(StoryChapterDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract StoryChapterDTO toDto(StoryChapter entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public StoryChapterDTO createDto(StoryChapter entity, @Context CycleAvoidingMappingContext context) {
        StoryChapterDTO existingDto = context.getMappedInstance(entity, StoryChapterDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return StoryChapterDTO.builder().build();
    }
}

package com.example.language_learning.mappers.mapstruct;

import com.example.language_learning.mappers.CycleAvoidingMappingContext;
import com.example.language_learning.lessonbook.chapter.dtos.ChapterDTO;
import com.example.language_learning.lessonbook.chapter.data.Chapter;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {PageStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class ChapterStructMapper {
    @Mapping(target = "lessonBook", ignore = true)
    public abstract Chapter toEntity(ChapterDTO dto, @Context CycleAvoidingMappingContext context);
    public abstract ChapterDTO toDto(Chapter entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ChapterDTO createDto(Chapter entity, @Context CycleAvoidingMappingContext context) {
        ChapterDTO existingDto = context.getMappedInstance(entity, ChapterDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ChapterDTO.builder().build();
    }
}

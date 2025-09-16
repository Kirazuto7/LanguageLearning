package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.entity.models.Chapter;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {PageStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface ChapterStructMapper {
    Chapter toEntity(ChapterDTO dto);
    ChapterDTO toDto(Chapter entity);
}

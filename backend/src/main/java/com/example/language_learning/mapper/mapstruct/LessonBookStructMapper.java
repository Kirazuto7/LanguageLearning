package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.LessonBookDTO;
import com.example.language_learning.entity.models.LessonBook;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {ChapterStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface LessonBookStructMapper {
    LessonBook toEntity(LessonBookDTO dto);
    LessonBookDTO toDto(LessonBook entity);
}

package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.SpanishWordDetailsDTO;
import com.example.language_learning.entity.models.details.SpanishWordDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpanishWordDetailsStructMapper {
    SpanishWordDetails toEntity(SpanishWordDetailsDTO dto);

    SpanishWordDetailsDTO toDto(SpanishWordDetails entity);
}
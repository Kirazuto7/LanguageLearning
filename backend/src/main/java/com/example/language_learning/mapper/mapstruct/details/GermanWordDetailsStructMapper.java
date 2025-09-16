package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.GermanWordDetailsDTO;
import com.example.language_learning.entity.models.details.GermanWordDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GermanWordDetailsStructMapper {
    GermanWordDetails toEntity(GermanWordDetailsDTO dto);

    GermanWordDetailsDTO toDto(GermanWordDetails entity);
}
package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.FrenchWordDetailsDTO;
import com.example.language_learning.entity.models.details.FrenchWordDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FrenchWordDetailsStructMapper {
    FrenchWordDetails toEntity(FrenchWordDetailsDTO dto);

    FrenchWordDetailsDTO toDto(FrenchWordDetails entity);
}
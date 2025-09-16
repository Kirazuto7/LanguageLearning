package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.ChineseWordDetailsDTO;
import com.example.language_learning.entity.models.details.ChineseWordDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChineseWordDetailsStructMapper {
    ChineseWordDetails toEntity(ChineseWordDetailsDTO dto);

    ChineseWordDetailsDTO toDto(ChineseWordDetails entity);
}
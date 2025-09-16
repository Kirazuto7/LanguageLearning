package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.ThaiWordDetailsDTO;
import com.example.language_learning.entity.models.details.ThaiWordDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ThaiWordDetailsStructMapper {
    ThaiWordDetails toEntity(ThaiWordDetailsDTO dto);

    ThaiWordDetailsDTO toDto(ThaiWordDetails entity);
}
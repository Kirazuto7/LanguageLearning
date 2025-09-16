package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.ItalianWordDetailsDTO;
import com.example.language_learning.entity.models.details.ItalianWordDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItalianWordDetailsStructMapper {
    ItalianWordDetails toEntity(ItalianWordDetailsDTO dto);

    ItalianWordDetailsDTO toDto(ItalianWordDetails entity);
}
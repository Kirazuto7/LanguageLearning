package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.JapaneseWordDetailsDTO;
import com.example.language_learning.entity.models.details.JapaneseWordDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JapaneseWordDetailsStructMapper {
    JapaneseWordDetails toEntity(JapaneseWordDetailsDTO dto);

    JapaneseWordDetailsDTO toDto(JapaneseWordDetails entity);
}

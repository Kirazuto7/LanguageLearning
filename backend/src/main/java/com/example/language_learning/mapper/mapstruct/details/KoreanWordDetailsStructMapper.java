package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.KoreanWordDetailsDTO;
import com.example.language_learning.entity.models.details.KoreanWordDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KoreanWordDetailsStructMapper {
    KoreanWordDetails toEntity(KoreanWordDetailsDTO dto);

    KoreanWordDetailsDTO toDto(KoreanWordDetails entity);
}

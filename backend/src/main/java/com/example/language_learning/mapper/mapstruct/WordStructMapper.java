package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.WordDTO;
import com.example.language_learning.entity.models.Word;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {WordDetailsStructMapper.class}
)
public interface WordStructMapper {
    Word toEntity(WordDTO dto);
    WordDTO toDto(Word entity);
}
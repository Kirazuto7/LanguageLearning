package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.SentenceDTO;
import com.example.language_learning.entity.models.Sentence;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring"
)
public interface SentenceStructMapper {
    Sentence toEntity(SentenceDTO dto);

    SentenceDTO toDto(Sentence entity);
}

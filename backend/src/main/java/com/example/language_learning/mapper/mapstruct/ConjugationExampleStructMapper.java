package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.entity.models.ConjugationExample;
import com.example.language_learning.entity.lessons.ConjugationLesson;
import com.example.language_learning.dto.models.ConjugationExampleDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring"
)
public interface ConjugationExampleStructMapper {
    @Mapping(target = "lesson", source = "lesson")
    ConjugationExample toEntity(ConjugationExampleDTO dto, @Context ConjugationLesson lesson);

    ConjugationExampleDTO toDto(ConjugationExample entity);
}

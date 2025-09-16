package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.lessons.GrammarLessonDTO;
import com.example.language_learning.entity.lessons.GrammarLesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {SentenceStructMapper.class},
    collectionMappingStrategy = org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED
)
public interface GrammarLessonStructMapper {
    @Mapping(target = "type", constant = "GRAMMAR")
    GrammarLesson toEntity(GrammarLessonDTO dto);

    GrammarLessonDTO toDto(GrammarLesson entity);
}

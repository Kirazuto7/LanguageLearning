package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.entity.lessons.VocabularyLesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {WordStructMapper.class}
)
public interface VocabularyLessonStructMapper {
    @Mapping(target = "type", constant = "VOCABULARY")
    VocabularyLesson toEntity(VocabularyLessonDTO dto);

    VocabularyLessonDTO toDto(VocabularyLesson entity);
}

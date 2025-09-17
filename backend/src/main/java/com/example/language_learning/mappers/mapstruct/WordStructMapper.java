package com.example.language_learning.mappers.mapstruct;

import com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos.WordDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.data.Word;
import com.example.language_learning.mappers.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(
        componentModel = "spring",
        uses = {WordDetailsStructMapper.class}
)
public abstract class WordStructMapper {
    public abstract Word toEntity(WordDTO dto, @Context CycleAvoidingMappingContext context);
    public abstract WordDTO toDto(Word entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public WordDTO createDto(Word entity, @Context CycleAvoidingMappingContext context) {
        WordDTO existingDto = context.getMappedInstance(entity, WordDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return WordDTO.builder().build();
    }
}
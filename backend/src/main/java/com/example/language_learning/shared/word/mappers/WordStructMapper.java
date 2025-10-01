package com.example.language_learning.shared.word.mappers;

import com.example.language_learning.shared.word.dtos.WordDTO;
import com.example.language_learning.shared.word.data.Word;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
        componentModel = "spring",
        uses = {WordDetailsStructMapper.class}
)
public abstract class WordStructMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
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
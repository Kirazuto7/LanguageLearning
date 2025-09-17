package com.example.language_learning.mappers.mapstruct.details;

import com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos.SpanishWordDetailsDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.data.SpanishWordDetails;
import com.example.language_learning.mappers.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class SpanishWordDetailsStructMapper {
    public abstract SpanishWordDetails toEntity(SpanishWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract SpanishWordDetailsDTO toDto(SpanishWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public SpanishWordDetailsDTO createDto(SpanishWordDetails entity, @Context CycleAvoidingMappingContext context) {
        SpanishWordDetailsDTO existingDto = context.getMappedInstance(entity, SpanishWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return SpanishWordDetailsDTO.builder().build();
    }
}
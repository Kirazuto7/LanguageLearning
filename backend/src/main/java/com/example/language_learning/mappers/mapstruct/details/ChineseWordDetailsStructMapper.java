package com.example.language_learning.mappers.mapstruct.details;

import com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos.ChineseWordDetailsDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.data.ChineseWordDetails;
import com.example.language_learning.mappers.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class ChineseWordDetailsStructMapper {
    public abstract ChineseWordDetails toEntity(ChineseWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract ChineseWordDetailsDTO toDto(ChineseWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ChineseWordDetailsDTO createDto(ChineseWordDetails entity, @Context CycleAvoidingMappingContext context) {
        ChineseWordDetailsDTO existingDto = context.getMappedInstance(entity, ChineseWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ChineseWordDetailsDTO.builder().build();
    }
}
package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.QuestionDTO;
import com.example.language_learning.entity.models.Question;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring"
)
public abstract class QuestionStructMapper {
    @Mapping(target="lesson", ignore = true)
    public abstract Question toEntity(QuestionDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract QuestionDTO toDto(Question entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public QuestionDTO createDto(Question entity, @Context CycleAvoidingMappingContext context) {
        QuestionDTO existingDto = context.getMappedInstance(entity, QuestionDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return QuestionDTO.builder().build();
    }
}

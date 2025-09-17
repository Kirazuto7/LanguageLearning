package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.ItalianWordDetailsDTO;
import com.example.language_learning.entity.models.details.ItalianWordDetails;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class ItalianWordDetailsStructMapper {
    public abstract ItalianWordDetails toEntity(ItalianWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract ItalianWordDetailsDTO toDto(ItalianWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ItalianWordDetailsDTO createDto(ItalianWordDetails entity, @Context CycleAvoidingMappingContext context) {
        ItalianWordDetailsDTO existingDto = context.getMappedInstance(entity, ItalianWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ItalianWordDetailsDTO.builder().build();
    }
}
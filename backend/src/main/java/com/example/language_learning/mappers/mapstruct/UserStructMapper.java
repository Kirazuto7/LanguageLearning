package com.example.language_learning.mappers.mapstruct;

import com.example.language_learning.mappers.CycleAvoidingMappingContext;
import com.example.language_learning.user.dtos.UserDTO;
import com.example.language_learning.user.data.User;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring",
    uses = {SettingsStructMapper.class, LessonBookStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class UserStructMapper {
    @Mapping(target = "password", ignore = true)
    public abstract User toEntity(UserDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract UserDTO toDto(User entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public UserDTO createDto(User entity, @Context CycleAvoidingMappingContext context) {
        UserDTO existingDto = context.getMappedInstance(entity, UserDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return UserDTO.builder().build();
    }
}

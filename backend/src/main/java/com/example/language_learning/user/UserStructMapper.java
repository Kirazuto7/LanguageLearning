package com.example.language_learning.user;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring",
    uses = {SettingsStructMapper.class}
)
public abstract class UserStructMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "refreshTokenExpiry", ignore = true)
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

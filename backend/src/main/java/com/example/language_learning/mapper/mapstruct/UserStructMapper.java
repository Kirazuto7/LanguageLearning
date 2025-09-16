package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.user.UserDTO;
import com.example.language_learning.entity.user.User;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {SettingsStructMapper.class, LessonBookStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface UserStructMapper {
    User toEntity(UserDTO dto);
    UserDTO toDto(User entity);
}

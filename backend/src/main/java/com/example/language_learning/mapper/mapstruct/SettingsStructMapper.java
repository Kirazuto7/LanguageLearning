package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.user.SettingsDTO;
import com.example.language_learning.entity.user.Settings;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingsStructMapper {
    Settings toEntity(SettingsDTO dto);
    SettingsDTO toDto(Settings entity);
}

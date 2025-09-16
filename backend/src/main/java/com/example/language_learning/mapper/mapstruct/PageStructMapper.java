package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.PageDTO;
import com.example.language_learning.entity.models.Page;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {LessonStructMapper.class}
)
public interface PageStructMapper {
    Page toEntity(PageDTO dto);
    PageDTO toDto(Page entity);
}

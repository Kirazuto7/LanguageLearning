package com.example.language_learning.lessonbook;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.lessonbook.chapter.LessonChapterStructMapper;
import com.example.language_learning.user.UserStructMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Context;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(
    componentModel = "spring",
    uses = {LessonChapterStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class LessonBookStructMapper {

    protected UserStructMapper userStructMapper;

    //@Mapping(target = "user", expression = "java(userStructMapper.toEntity(dto.user(), context))")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract LessonBook toEntity(LessonBookDTO dto, @Context CycleAvoidingMappingContext context);

    //@Mapping(target = "user", ignore = true) // Manually mapped in the factory to handle cycles
    public abstract LessonBookDTO toDto(LessonBook entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public LessonBookDTO createDto(LessonBook entity, @Context CycleAvoidingMappingContext context) {
        // Check if an instance already exists in the context
        LessonBookDTO existingDto = context.getMappedInstance(entity, LessonBookDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        // Create a new DTO, store it in the context, and then return it for MapStruct to populate
        return LessonBookDTO.builder().build();
    }

    @Autowired
    public void setUserStructMapper(@Lazy UserStructMapper userStructMapper) {
        this.userStructMapper = userStructMapper;
    }
}

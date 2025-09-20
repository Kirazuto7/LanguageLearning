package com.example.language_learning.shared.word.mappers;

import com.example.language_learning.shared.word.dtos.*;
import com.example.language_learning.shared.word.data.*;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.SubclassMapping;

@Mapper(
        componentModel = "spring",
        uses = {
            JapaneseWordDetailsStructMapper.class,
            KoreanWordDetailsStructMapper.class,
            ChineseWordDetailsStructMapper.class,
            ThaiWordDetailsStructMapper.class,
            ItalianWordDetailsStructMapper.class,
            SpanishWordDetailsStructMapper.class,
            FrenchWordDetailsStructMapper.class,
            GermanWordDetailsStructMapper.class
        }
)
public abstract class WordDetailsStructMapper {

    @ObjectFactory
    public WordDetails createWordDetails(WordDetailsDTO dto, @Context CycleAvoidingMappingContext context) {
        WordDetails existingEntity = context.getMappedInstance(dto, WordDetails.class);
        if (existingEntity != null) {
            return existingEntity;
        }

        return switch (dto) {
            case null -> null;
            case JapaneseWordDetailsDTO j -> JapaneseWordDetails.builder().build();
            case KoreanWordDetailsDTO k -> KoreanWordDetails.builder().build();
            case ChineseWordDetailsDTO c -> ChineseWordDetails.builder().build();
            case ThaiWordDetailsDTO t -> ThaiWordDetails.builder().build();
            case ItalianWordDetailsDTO i -> ItalianWordDetails.builder().build();
            case SpanishWordDetailsDTO s -> SpanishWordDetails.builder().build();
            case FrenchWordDetailsDTO f -> FrenchWordDetails.builder().build();
            case GermanWordDetailsDTO g -> GermanWordDetails.builder().build();
            default -> throw new IllegalArgumentException("Unknown DTO type: " + dto.getClass());
        };
    }

    @ObjectFactory
    public WordDetailsDTO createWordDetailsDTO(WordDetails entity, @Context CycleAvoidingMappingContext context) {
        WordDetailsDTO existingDto = context.getMappedInstance(entity, WordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }

        return switch (entity) {
            case null -> null;
            case JapaneseWordDetails j -> JapaneseWordDetailsDTO.builder().build();
            case KoreanWordDetails k -> KoreanWordDetailsDTO.builder().build();
            case ChineseWordDetails c -> ChineseWordDetailsDTO.builder().build();
            case ThaiWordDetails t -> ThaiWordDetailsDTO.builder().build();
            case ItalianWordDetails i -> ItalianWordDetailsDTO.builder().build();
            case SpanishWordDetails s -> SpanishWordDetailsDTO.builder().build();
            case FrenchWordDetails f -> FrenchWordDetailsDTO.builder().build();
            case GermanWordDetails g -> GermanWordDetailsDTO.builder().build();
            default -> throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
        };
    }

    @SubclassMapping(source = JapaneseWordDetailsDTO.class, target = JapaneseWordDetails.class)
    @SubclassMapping(source = KoreanWordDetailsDTO.class, target = KoreanWordDetails.class)
    @SubclassMapping(source = ChineseWordDetailsDTO.class, target = ChineseWordDetails.class)
    @SubclassMapping(source = ThaiWordDetailsDTO.class, target = ThaiWordDetails.class)
    @SubclassMapping(source = ItalianWordDetailsDTO.class, target = ItalianWordDetails.class)
    @SubclassMapping(source = SpanishWordDetailsDTO.class, target = SpanishWordDetails.class)
    @SubclassMapping(source = FrenchWordDetailsDTO.class, target = FrenchWordDetails.class)
    @SubclassMapping(source = GermanWordDetailsDTO.class, target = GermanWordDetails.class)
    public abstract WordDetails toEntity(WordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    @SubclassMapping(source = JapaneseWordDetails.class, target = JapaneseWordDetailsDTO.class)
    @SubclassMapping(source = KoreanWordDetails.class, target = KoreanWordDetailsDTO.class)
    @SubclassMapping(source = ChineseWordDetails.class, target = ChineseWordDetailsDTO.class)
    @SubclassMapping(source = ThaiWordDetails.class, target = ThaiWordDetailsDTO.class)
    @SubclassMapping(source = ItalianWordDetails.class, target = ItalianWordDetailsDTO.class)
    @SubclassMapping(source = SpanishWordDetails.class, target = SpanishWordDetailsDTO.class)
    @SubclassMapping(source = FrenchWordDetails.class, target = FrenchWordDetailsDTO.class)
    @SubclassMapping(source = GermanWordDetails.class, target = GermanWordDetailsDTO.class)
    public abstract WordDetailsDTO toDto(WordDetails entity, @Context CycleAvoidingMappingContext context);
}
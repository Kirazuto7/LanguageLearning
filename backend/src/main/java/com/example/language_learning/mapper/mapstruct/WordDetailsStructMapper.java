package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.WordDetailsDTO;
import com.example.language_learning.dto.models.details.*;
import com.example.language_learning.entity.models.WordDetails;
import com.example.language_learning.entity.models.details.*;
import com.example.language_learning.mapper.mapstruct.details.*;
import org.mapstruct.Mapper;
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
public interface WordDetailsStructMapper {

    @SubclassMapping(source = JapaneseWordDetailsDTO.class, target = JapaneseWordDetails.class)
    @SubclassMapping(source = KoreanWordDetailsDTO.class, target = KoreanWordDetails.class)
    @SubclassMapping(source = ChineseWordDetailsDTO.class, target = ChineseWordDetails.class)
    @SubclassMapping(source = ThaiWordDetailsDTO.class, target = ThaiWordDetails.class)
    @SubclassMapping(source = ItalianWordDetailsDTO.class, target = ItalianWordDetails.class)
    @SubclassMapping(source = SpanishWordDetailsDTO.class, target = SpanishWordDetails.class)
    @SubclassMapping(source = FrenchWordDetailsDTO.class, target = FrenchWordDetails.class)
    @SubclassMapping(source = GermanWordDetailsDTO.class, target = GermanWordDetails.class)
    WordDetails toEntity(WordDetailsDTO dto);

    @SubclassMapping(source = JapaneseWordDetails.class, target = JapaneseWordDetailsDTO.class)
    @SubclassMapping(source = KoreanWordDetails.class, target = KoreanWordDetailsDTO.class)
    @SubclassMapping(source = ChineseWordDetails.class, target = ChineseWordDetailsDTO.class)
    @SubclassMapping(source = ThaiWordDetails.class, target = ThaiWordDetailsDTO.class)
    @SubclassMapping(source = ItalianWordDetails.class, target = ItalianWordDetailsDTO.class)
    @SubclassMapping(source = SpanishWordDetails.class, target = SpanishWordDetailsDTO.class)
    @SubclassMapping(source = FrenchWordDetails.class, target = FrenchWordDetailsDTO.class)
    @SubclassMapping(source = GermanWordDetails.class, target = GermanWordDetailsDTO.class)
    WordDetailsDTO toDto(WordDetails entity);
}
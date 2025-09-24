package com.example.language_learning.config;

import com.example.language_learning.lessonbook.chapter.lesson.dtos.LessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageDTO;
import com.example.language_learning.shared.enums.LessonType;
import com.example.language_learning.shared.word.dtos.*;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import graphql.scalars.ExtendedScalars;
import graphql.schema.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;


@Configuration
public class GraphqlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        TypeResolver lessonTypeResolver = env -> {
            Object javaObject = env.getObject();
            if (javaObject instanceof LessonDTO lessonDTO) {
                return switch (lessonDTO.type()) {
                    case LessonType.VOCABULARY -> env.getSchema().getObjectType("VocabularyLesson");
                    case LessonType.GRAMMAR -> env.getSchema().getObjectType("GrammarLesson");
                    case LessonType.PRACTICE -> env.getSchema().getObjectType("PracticeLesson");
                    case LessonType.CONJUGATION -> env.getSchema().getObjectType("ConjugationLesson");
                    case LessonType.READING_COMPREHENSION -> env.getSchema().getObjectType("ReadingComprehensionLesson");
                    default -> null;
                };
            }
            return null;
        };

        TypeResolver progressDataTypeResolver = env -> {
            Object javaObject = env.getObject();
            if (javaObject instanceof LessonPageDTO) {
                return env.getSchema().getObjectType("LessonPage");
            }
            else if  (javaObject instanceof StoryPageDTO storyPage) {
                if (storyPage.paragraphs() != null && !storyPage.paragraphs().isEmpty()) {
                    return env.getSchema().getObjectType("StoryContentPage");
                }
                else if (storyPage.vocabulary() != null && !storyPage.vocabulary().isEmpty()) {
                    return env.getSchema().getObjectType("StoryVocabularyPage");
                }
            }
            return null;
        };

        TypeResolver storyPageTypeResolver = env -> {
            Object javaObject = env.getObject();
            if (javaObject instanceof StoryPageDTO storyPage) {
                return switch (storyPage) {
                    case StoryPageDTO p when
                        p.paragraphs() != null && !p.paragraphs().isEmpty() ->
                            env.getSchema().getObjectType("StoryContentPage");
                    case StoryPageDTO p when
                        p.vocabulary() != null && !p.vocabulary().isEmpty() ->
                            env.getSchema().getObjectType("StoryVocabularyPage");
                    default ->
                        null;
                };
            }
            return null;
        };

        TypeResolver wordDetailsTypeResolver = env -> {
            Object javaObject = env.getObject();
            return switch (javaObject) {
                case JapaneseWordDetailsDTO j -> env.getSchema().getObjectType("JapaneseWordDetails");
                case KoreanWordDetailsDTO k -> env.getSchema().getObjectType("KoreanWordDetails");
                case ChineseWordDetailsDTO c -> env.getSchema().getObjectType("ChineseWordDetails");
                case ThaiWordDetailsDTO t -> env.getSchema().getObjectType("ThaiWordDetails");
                case ItalianWordDetailsDTO i -> env.getSchema().getObjectType("ItalianWordDetails");
                case SpanishWordDetailsDTO s -> env.getSchema().getObjectType("SpanishWordDetails");
                case FrenchWordDetailsDTO f -> env.getSchema().getObjectType("FrenchWordDetails");
                case GermanWordDetailsDTO g -> env.getSchema().getObjectType("GermanWordDetails");
                default -> null;
            };
        };

        return wiringBuilder -> wiringBuilder
            .scalar(ExtendedScalars.Json)
            .type("Lesson", typeWiring -> typeWiring.typeResolver(lessonTypeResolver))
            .type("ProgressData", typeWiring -> typeWiring.typeResolver(progressDataTypeResolver))
            .type("StoryPage", typeWiring -> typeWiring.typeResolver(storyPageTypeResolver))
            .type("WordDetails", typeWiring -> typeWiring.typeResolver(wordDetailsTypeResolver));
    }
}

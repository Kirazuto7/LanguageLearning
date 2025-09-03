import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import { LessonBookDTO, LessonBookRequest } from "../../types/dto";

export const lessonBookApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({

        // Query to fetch all of the user's lesson books
        getLessonBooks: builder.query<LessonBookDTO[], void>({
            query: () => ({
                body: gql`
                    query GetLessonBooks {
                        getLessonBooks {
                            id
                            bookTitle
                            difficulty
                            language
                        }
                    }
                `
            }),
            transformResponse: (response: { getLessonBooks: LessonBookDTO[] }) => response.getLessonBooks,
            providesTags: (result = []) => [
                { type: 'Book', id: 'LIST' },
                ...result.map(({ id }) => ({ type: 'Book' as const, id: String(id) })),
            ],
        }),

        // Query to Fetch a Specific LessonBook based on language and difficulty
        getLessonBook: builder.query<LessonBookDTO, LessonBookRequest>({
            query: (request) => ({
                body: gql`
                    query GetLessonBook($request: LessonBookRequestInput!) {
                        getLessonBook(request: $request) {
                            id
                            bookTitle
                            difficulty
                            language
                            chapters {
                                id
                                title
                                nativeTitle
                                chapterNumber
                                pages {
                                    id
                                    pageNumber
                                    lesson {
                                        id
                                        type
                                        title
                                        ... on VocabularyLesson {
                                            vocabularies {
                                                id
                                                nativeWord
                                                englishTranslation
                                                phoneticSpelling
                                            }
                                        }
                                        ... on GrammarLesson {
                                            grammarConcept
                                            nativeGrammarConcept
                                            explanation
                                            exampleSentences {
                                                id
                                                text
                                                translation
                                            }
                                        }
                                        ... on ConjugationLesson {
                                            conjugationRuleName
                                            explanation
                                            conjugatedWords {
                                                id
                                                infinitive
                                                conjugatedForm
                                                exampleSentence
                                                sentenceTranslation
                                            }
                                        }
                                        ... on PracticeLesson {
                                            instructions
                                            questions {
                                                id
                                                questionType
                                                questionText
                                                answerChoices
                                                answer
                                            }
                                        }
                                        ... on ReadingComprehensionLesson {
                                            story
                                            questions {
                                                id
                                                questionText
                                                questionType
                                                answerChoices
                                                answer
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                `,
                variables: { request },
            }),
            transformResponse: (response: { getLessonBook: LessonBookDTO }) => response.getLessonBook,
            providesTags: (result) => (result ? [{ type: 'Book', id: String(result.id) }] : []),
        }),

    })
});

export const { useGetLessonBooksQuery, useGetLessonBookQuery } = lessonBookApiSlice;

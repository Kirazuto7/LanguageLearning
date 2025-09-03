import { graphqlApiSlice } from "./graphqlApiSlice";
import { gql } from "graphql-request";
import {ChapterDTO, ChapterGenerationRequest, ProgressUpdateDTO, LessonBookRequest } from "../../types/dto";
import { createClient } from "graphql-ws";
import { lessonBookApiSlice } from "./lessonBookApiSlice";

export const chapterApiSlice = graphqlApiSlice.injectEndpoints({
    endpoints: builder => ({
        // Query to fetch a Chapter by id
        getChapter: builder.query<ChapterDTO, number>({
            query:(id) => ({
                body: gql`
                    query GetChapter($id: ID!) {
                        getChapterById(id: $id) {
                            id
                            chapterNumber
                            title
                            nativeTitle
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
                `,
                variables: { id },
            }),
            transformResponse: (response: { getChapterById: ChapterDTO }) => response.getChapterById,
            providesTags: (result) => (result ? [{ type: 'Chapter', id: String(result.id) }] : []),
        }),

        // Query endpoint to handle the real-time progress subscription for a new chapter generation
        chapterGenerationProgress: builder.query<ProgressUpdateDTO, string>({
            query: () => ({
                body: `query { __typename }`,
            }),
            async onCacheEntryAdded(
                taskId,
                { updateCachedData, cacheDataLoaded, cacheEntryRemoved }
            ) {
                const client = createClient({
                    url: 'ws://localhost:8080/graphql',
                });

                await cacheDataLoaded;

                const unsubscribe = client.subscribe(
                    {
                        query: gql`
                            subscription ChapterGenerationProgress($taskId: ID!) {
                                chapterGenerationProgress(taskId: $taskId) {
                                    taskId
                                    progress
                                    message
                                    chapterId
                                    data {
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
                                    error
                                }
                            }
                        `,
                        variables: { taskId },
                    },
                    {
                        next: (result: { data?: { chapterGenerationProgress?: ProgressUpdateDTO } }) => {
                            const data = result.data?.chapterGenerationProgress;
                            if (data) {
                                updateCachedData(() => data);
                            }
                        },
                        error: (error) => { console.error('Subscription error:', error); },
                        complete: () => {},
                    }
                );

                await cacheEntryRemoved;
                unsubscribe();
            }
        }),

        // Mutation to generate a new Chapter
        generateChapter: builder.mutation<{ taskId: string; chapter: ChapterDTO }, ChapterGenerationRequest>({
            query: ({ language, difficulty, topic }) => ({
                body: gql`
                    mutation GenerateChapter($request: ChapterGenerationRequestInput!) {
                        generateChapter(request: $request) {
                            taskId
                            chapter {
                                id
                                chapterNumber
                                title
                                nativeTitle
                                pages { id } # Initially empty
                            }
                        }
                    }
               `,
                variables: { request: { language, difficulty, topic } },
            }),
            transformResponse: (response: { generateChapter: { taskId: string; chapter: ChapterDTO } }) => response.generateChapter,
            async onQueryStarted({ language, difficulty }, { dispatch, queryFulfilled }) {
                try {
                    const { data: { chapter: newChapter } } = await queryFulfilled;

                    dispatch(
                        lessonBookApiSlice.util.updateQueryData('getLessonBook', { language, difficulty }, (draft) => {
                            draft.chapters.push(newChapter);
                        })
                    );
                }
                catch {}
            }
        }),

    })
});

export const { useGetChapterQuery, useLazyGetChapterQuery, useLazyChapterGenerationProgressQuery, useChapterGenerationProgressQuery, useGenerateChapterMutation } = chapterApiSlice;
import React from 'react';
import { GrammarLessonDTO, SentenceDTO } from '../../../../types/dto';

interface GrammarLessonProps {
    lesson: GrammarLessonDTO;
}

/**
 * Renders the content for a grammar lesson, displaying the grammar concept and example sentences.
*/
const GrammarLesson: React.FC<GrammarLessonProps> = ({ lesson }) => {
    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            <p>{lesson.grammarConcept}</p>
            <p>{lesson.nativeGrammarConcept}</p>
            {
                lesson.exampleSentences.map((sentence: SentenceDTO, index) => (
                    <div key={sentence.id ?? index}>
                        <p>{sentence.text}</p>
                        <p>{sentence.translation}</p>
                    </div>
                ))
            }
        </div>
    );
};

export default GrammarLesson;
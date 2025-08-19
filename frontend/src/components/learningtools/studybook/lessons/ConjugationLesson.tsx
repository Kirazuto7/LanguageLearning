import React from 'react';
import { ConjugationLessonDTO, ConjugationExampleDTO } from '../../../../types/dto';
import { Card } from 'react-bootstrap';

interface ConjugationLessonProps {
    lesson: ConjugationLessonDTO;
}

/**
 * Renders the content for a conjugation grammar lesson, displaying the conjugation table & example sentences.
*/
const ConjugationLesson: React.FC<ConjugationLessonProps> = ({ lesson }) => {
    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            <p>{lesson.conjugationRuleName}</p>
            <p>{lesson.explanation}</p>
            {
                lesson.conjugatedWords.map((word: ConjugationExampleDTO, index) => (
                    <Card key={word.id ?? index}>
                        <Card.Header>
                            {word.infinitive}
                        </Card.Header>
                        <Card.Body>
                            <div className="mb-3">Conjugated Form: {word.conjugatedForm}</div>
                            <div>Example: </div>
                            <div>{word.exampleSentence}</div>
                            <div>{word.sentenceTranslation}</div>
                        </Card.Body>
                    </Card>
                ))
            }
        </div>
    );
};

export default ConjugationLesson;
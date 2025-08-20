import React from 'react';
import { ConjugationLessonDTO, ConjugationExampleDTO } from '../../../../types/dto';
import { Card } from 'react-bootstrap';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';

interface ConjugationLessonProps {
    lesson: ConjugationLessonDTO;
}

/**
 * Renders the content for a conjugation grammar lesson, displaying the conjugation table & example sentences.
*/
const ConjugationLesson: React.FC<ConjugationLessonProps> = ({ lesson }) => {
    const {settings} = useSettingsManager();
    const isJapanese = settings?.language.toLowerCase() === "japanese";
    

    const renderText = (text: string, { as: Component = 'div', className = '' } = {}) => {
        if (isJapanese) {
            return <Component className={className} dangerouslySetInnerHTML={{ __html: text }} />;
        }
        return <Component className={className}>{text}</Component>;
    };

    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            <p>{lesson.conjugationRuleName}</p>
            <p>{lesson.explanation}</p>
            {
                lesson.conjugatedWords.map((word: ConjugationExampleDTO, index) => (
                    <Card key={word.id ?? index}>
                        <Card.Header>
                            {renderText(word.infinitive, { as: 'strong' })}
                        </Card.Header>
                        <Card.Body>
                            <div className="d-flex align-items-center mb-3">
                                <span className="me-2">Conjugated Form:</span>
                                {renderText(word.conjugatedForm, { as: 'span' })}
                            </div>
                            <div className="mb-1">Example:</div>
                            {renderText(word.exampleSentence, { className: 'lead' })}
                            <div className="text-muted"><em>{word.sentenceTranslation}</em></div>
                        </Card.Body>
                    </Card>
                ))
            }
        </div>
    );
};

export default ConjugationLesson;
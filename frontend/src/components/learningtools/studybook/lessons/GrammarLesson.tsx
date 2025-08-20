import React from 'react';
import { GrammarLessonDTO, SentenceDTO } from '../../../../types/dto';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';

interface GrammarLessonProps {
    lesson: GrammarLessonDTO;
}

/**
 * Renders the content for a grammar lesson, displaying the grammar concept and example sentences.
*/
const GrammarLesson: React.FC<GrammarLessonProps> = ({ lesson }) => {
    const { settings } = useSettingsManager();
    const isJapanese = settings?.language.toLowerCase() === 'japanese';

    const renderText = (text: string, { as: Component = 'div', className = '' } = {}) => {
        if (isJapanese) {
            return <Component className={className} dangerouslySetInnerHTML={{ __html: text }} />;
        }
        return <Component className={className}>{text}</Component>;
    };

    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            <p>{lesson.grammarConcept}</p>
            <p>{lesson.nativeGrammarConcept}</p>
            <hr />
            <h6 className="mt-3">Examples:</h6>
            {
                lesson.exampleSentences.map((sentence: SentenceDTO, index) => (
                    <div key={sentence.id ?? index} className="mb-3">
                        {renderText(sentence.text, { as: 'p', className: 'lead' })}
                        <p className="text-muted"><em>{sentence.translation}</em></p>
                    </div>
                ))
            }
        </div>
    );
};

export default GrammarLesson;
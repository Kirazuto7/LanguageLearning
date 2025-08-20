import React from 'react';
import { PracticeLessonDTO, QuestionDTO } from '../../../../types/dto';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';

interface PracticeLessonProps {
    lesson: PracticeLessonDTO;
}

/**
 * Renders the content for a practice lesson, displaying open-ended questions for the user.
*/
const PracticeLesson: React.FC<PracticeLessonProps> = ({ lesson }) => {
    const {settings} = useSettingsManager();
    const isJapanese = settings?.language.toLowerCase() === "japanese";

    const renderText = (
        text: string,
        { as: Component = 'p' as React.ElementType, className = '' } = {}
    ) => {
        if (isJapanese) {
            return <Component className={className} dangerouslySetInnerHTML={{ __html: text }} />;
        }
        return <Component className={className}>{text}</Component>;
    };

    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            <p>{lesson.instructions}</p>
            {lesson.questions.map((question: QuestionDTO, index) => (
                <div key={question.id ?? index} className="mb-3">
                    {renderText(question.questionText, { className: 'lead' })}
                </div>
            ))}
        </div>
    );
};

export default PracticeLesson;
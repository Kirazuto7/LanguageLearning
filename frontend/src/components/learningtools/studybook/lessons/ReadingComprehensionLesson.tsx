import React from 'react';
import { ReadingComprehensionLessonDTO, QuestionDTO } from '../../../../types/dto';
import { useSettingsManager } from '../../../../hooks/useSettingsManager';

interface ReadingComprehensionLessonProps {
    lesson: ReadingComprehensionLessonDTO;
}

/**
 * Renders the content for a reading comprehension lesson, displaying the story and questions.
*/
const ReadingComprehensionLesson: React.FC<ReadingComprehensionLessonProps> = ({ lesson }) => {
    const {settings} = useSettingsManager();
    const isJapanese = settings?.language.toLowerCase() === "japanese";

    const renderText = (text: string, { as: Component = 'p', className = '' } = {}) => {
        if (isJapanese) {
            return <Component className={className} dangerouslySetInnerHTML={{ __html: text }} />;
        }
        return <Component className={className}>{text}</Component>;
    };

    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            {renderText(lesson.story, { className: 'lead' })}
            
            <hr />
            <h6 className="mt-3">Questions:</h6>
            {lesson.questions.map((question: QuestionDTO, index) => (
                <p key={question.id ?? index} className="mb-2">{index + 1}. {question.questionText}</p>
            ))}
        </div>
    );
};

export default ReadingComprehensionLesson;
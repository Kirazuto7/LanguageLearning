import React from 'react';
import { ReadingComprehensionLessonDTO, QuestionDTO } from '../../../../types/dto';

interface ReadingComprehensionLessonProps {
    lesson: ReadingComprehensionLessonDTO;
}

/**
 * Renders the content for a grammar lesson, displaying the grammar concept and example sentences.
*/
const ReadingComprehensionLesson: React.FC<ReadingComprehensionLessonProps> = ({ lesson }) => {
    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            <p>{lesson.story}</p>
            
            {
                lesson.questions.map((question: QuestionDTO, index) => (
                    <div key={question.id ?? index}>
                        <p>{question.questionText}</p>
                    </div>
                ))
            }
        </div>
    );
};

export default ReadingComprehensionLesson;
import React from 'react';
import { PracticeLessonDTO, QuestionDTO } from '../../../../types/dto';

interface PracticeLessonProps {
    lesson: PracticeLessonDTO;
}

/**
 * Renders the content for a grammar lesson, displaying the grammar concept and example sentences.
*/
const PracticeLesson: React.FC<PracticeLessonProps> = ({ lesson }) => {
    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            
            <p>{lesson.instructions}</p>
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

export default PracticeLesson;
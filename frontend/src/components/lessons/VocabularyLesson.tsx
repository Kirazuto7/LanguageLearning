import React from 'react';
import { VocabularyLessonDTO } from '../../types/dto';
import { renderWord } from '../../utils/renderUtils';

interface VocabularyLessonProps {
    lesson: VocabularyLessonDTO;
}

/**
 * Renders the content for a vocabulary lesson, displaying words and their translations in a table.
*/
const VocabularyLesson: React.FC<VocabularyLessonProps> = ({ lesson }) => {
    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            <table className="table table-striped">
                <tbody>
                    {lesson.vocabularies.map((vocabItem) => (
                        <tr key={vocabItem.id}>
                            <td><strong>{renderWord(vocabItem.word)}</strong></td>
                            <td>{vocabItem.word.translation}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default VocabularyLesson;




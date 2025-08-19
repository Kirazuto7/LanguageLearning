import React from 'react';
import { VocabularyLessonDTO, WordDTO } from '../../../types/dto';
import { renderWord } from '../../../utils/renderUtils';

interface VocabularyPageProps {
    lesson: VocabularyLessonDTO;
}

/**
 * Renders the content for a vocabulary lesson, displaying words and their translations in a table.
*/
const VocabularyPage: React.FC<VocabularyPageProps> = ({ lesson }) => {
    return (
        <div>
            <h5 className="text-center mb-4">{lesson.title}</h5>
            <table className="table table-striped">
                <tbody>
                    {lesson.vocabularies.map((vocabItem: WordDTO, index) => (
                        // Use the item's ID if it exists, otherwise fall back to the array index.
                        <tr key={vocabItem.id ?? index}>
                            <td><strong>{renderWord(vocabItem)}</strong></td>
                            <td>{vocabItem.englishTranslation}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default VocabularyPage;

import React from 'react';

/**
 * Renders the content for a vocabulary lesson.
 * @param {object} lesson - The lesson object containing title and vocabulary items.
 */
const VocabularyLesson = ({ lesson }) => {
    return (
        <div>
            <h4 className="text-center mb-4">{lesson.title}</h4>
            <table className="table table-striped">
                <tbody>
                    {lesson.items.map((item, itemIndex) => (
                        <tr key={`${item.word}-${itemIndex}`}>
                            <td><strong>{item.word}</strong></td>
                            <td>{item.translation}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default VocabularyLesson;


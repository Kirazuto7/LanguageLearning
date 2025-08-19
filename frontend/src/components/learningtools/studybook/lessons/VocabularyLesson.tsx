import React from 'react';
import { Table, Card } from 'react-bootstrap';
import { VocabularyLessonDTO, WordDTO } from '../../../../types/dto';

interface VocabularyLessonProps {
    lesson: VocabularyLessonDTO;
}

/**
 * Renders the content for a vocabulary lesson, displaying words and their translations in a table.
*/
const VocabularyLesson: React.FC<VocabularyLessonProps> = ({ lesson }) => {

    const isJapanese = lesson.vocabularies.length > 0 && lesson.vocabularies[0].language.toLowerCase() === 'japanese';

    return(
        <Card className="h-100 d-flex flex-column">
            <Card.Header as="h4" className="text-center">
                {lesson.title}
            </Card.Header>

            <Card.Body style={{ overflowY: 'auto' }}>
                <Table striped bordered hover responsive>
                    <thead>
                        {isJapanese ? (
                            <tr>
                                <th>Kanji</th>
                                <th>Hiragana</th>
                                <th>Katakana</th>
                                <th>Translation</th>
                            </tr>
                        ) : (
                            <tr>
                                <th>Word</th>
                                <th>Translation</th>
                            </tr>
                        )}
                    </thead>
                    <tbody>
                        {lesson.vocabularies.map((word: WordDTO, index) => (
                            <tr key={index}>
                                {isJapanese ? (
                                    <>
                                        <td>
                                            {word.details?.kanji ? (
                                                <ruby>
                                                    {word.details.kanji}
                                                    <rt>{word.details.hiragana}</rt>
                                                </ruby>
                                            ) : '—'}
                                        </td>
                                        <td>{word.details?.hiragana || '—'}</td>
                                        <td>{word.details?.katakana || '—'}</td>
                                    </>
                                ) : (
                                    <td>{word.nativeWord}</td>
                                )}
                                <td>{word.englishTranslation}</td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </Card.Body>
        </Card>
    );
}
export default VocabularyLesson;

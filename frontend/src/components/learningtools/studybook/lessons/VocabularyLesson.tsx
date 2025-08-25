import React from 'react';
import { Table, Card } from 'react-bootstrap';
import { VocabularyLessonDTO, WordDTO } from '../../../../types/dto';
import styles from "./lesson.module.scss";

interface VocabularyLessonProps {
    lesson: VocabularyLessonDTO;
}

/**
 * Renders the content for a vocabulary lesson, displaying words and their translations in a table.
*/
const VocabularyLesson: React.FC<VocabularyLessonProps> = ({ lesson }) => {

    const isJapanese = lesson.vocabularies.length > 0 && lesson.vocabularies[0].language.toLowerCase() === 'japanese';



    const renderDefaultHeader = () => (
        <tr>
            <th/>
            <th>Word</th>
            <th>Translation</th>
        </tr>
    );

    const renderDefaultRow = (word: WordDTO) => (
        <td>{word.nativeWord}</td>
    );

    const renderJapaneseHeader = () => (
        <tr>
            <th>Kanji</th>
            <th>Hiragana</th>
            <th>Katakana</th>
            <th>Translation</th>
        </tr>
    );

    const renderJapaneseRow = (word: WordDTO) => (
        <>
            <td>
                {word.details?.kanji ? (
                    <ruby>
                        {word.details.kanji}
                        <rt>{word.details.hiragana}</rt>
                    </ruby>
                ) : '-'}
            </td>
            <td>{word.details?.hiragana || '-'}</td>
            <td>{word.details?.katakana || '-'}</td>
        </>
    );

    return(
        <Card className="h-100 d-flex flex-column">
            <Card.Header as="h4" className="text-center">
                {lesson.title}
            </Card.Header>

            <Card.Body style={{ overflowY: 'auto' }}>
                <Table id={styles.vocabTable} striped bordered hover responsive>
                    <thead>
                        {isJapanese ? renderJapaneseHeader() : renderDefaultHeader()}
                    </thead>
                    <tbody>
                        {lesson.vocabularies.map((word: WordDTO, index) => (
                            <tr key={index}>
                                <td>{index + 1}.</td>
                                {isJapanese ? renderJapaneseRow(word) : renderDefaultRow(word)}
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

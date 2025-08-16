import React from 'react';
import { Table, Card } from 'react-bootstrap';
import { VocabularyLessonDTO, AnyWordDTO } from '../../../../types/dto';
import { renderWord } from '../../../../utils/renderUtils';

interface VocabularyLessonProps {
    lesson: VocabularyLessonDTO;
}

/**
 * Renders the content for a vocabulary lesson, displaying words and their translations in a table.
*/
const VocabularyLesson: React.FC<VocabularyLessonProps> = ({ lesson }) => {

    return(
        <Card className="h-100 d-flex flex-column">
            <Card.Header as="h4" className="text-center">
                {lesson.title}
            </Card.Header>

            <Card.Body style={{ overflowY: 'auto' }}>
                <Table striped bordered hover responsive>
                    <thead>
                        <tr>
                            <th>Word</th>
                            <th>Translation</th>
                        </tr>
                    </thead>
                    <tbody>
                        {lesson.vocabularies.map((word: AnyWordDTO, index) => (
                            <tr key={index}>
                                {renderWord(word)}
                                <td>{word.translation}</td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </Card.Body>
        </Card>
    );
}
export default VocabularyLesson;

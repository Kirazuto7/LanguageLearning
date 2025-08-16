import React from 'react';
import { Table, Card } from 'react-bootstrap';
import { VocabularyLessonDTO } from '../../types/dto';
import { renderWord } from '../../utils/renderUtils';


interface VocabularyPageProps {
    lesson: VocabularyLessonDTO;
}

const VocabularyPage: React.FC<VocabularyPageProps> = ({ lesson }) => {

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
                        {lesson.vocabularies.map((word, index) => (
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
export default VocabularyPage;
import {PracticeLessonCheckResponse} from "../../../../shared/types/dto";
import React from "react";
import {Alert, Spinner} from "react-bootstrap";

interface FeedbackDisplayProps {
    feedback: PracticeLessonCheckResponse | null;
    isLoading: boolean;
}

const FeedbackDisplay: React.FC<FeedbackDisplayProps> = ({ feedback, isLoading }) => {
    if (isLoading) {
        return (
            <div className="d-flex align-items-center mt-3 text-muted fst-italic">
                <Spinner animation="border" size="sm" className="me-2"/>
                <span>Checking...</span>
            </div>
        );
    }

    if (!feedback) {
        return <></>;
    }

    const { isCorrect, correctedSentence , feedback: feedbackText } = feedback;

    return(
        <Alert
            variant={isCorrect ? 'success' : 'warning'}
            className="mt-3"
        >
            <div className="d-flex align-items-center mb-2">
                {isCorrect ?
                    <i className="bi bi-check-circle-fill me-2"/> :
                    <i className="bi-exclamation-triangle-fill me-2"/>
                }
                <h6 className="mb-0">{isCorrect ? "Great job!" : "Here's a suggestion:"}</h6>
            </div>
            {correctedSentence && <p className="mb-1"><strong>Correction:</strong> <span className="text-primary">{correctedSentence}</span></p>}
            <p className="mb-0"><strong>Feedback:</strong> {feedbackText}</p>
        </Alert>
    );
};

export default FeedbackDisplay;
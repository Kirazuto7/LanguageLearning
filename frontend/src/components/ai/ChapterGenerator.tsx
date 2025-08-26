import React, {useEffect} from 'react';
import StudyBookMascot from './StudyBookMascot';
import {useSettingsManager} from "../../hooks/useSettingsManager";
import {useChapterGeneration} from "../../hooks/useChapterGeneration";
import {ChapterGenerationRequest} from "../../types/dto";
import {useSelector} from "react-redux";
import {RootState} from "../../app/store";
import {Alert} from "react-bootstrap";

interface ChapterGeneratorProps {
    celebrationTrigger?: number;
}

const ChapterGenerator: React.FC<ChapterGeneratorProps> = ({ celebrationTrigger }) => {
    const { user } = useSelector((state: RootState) => state.auth);
    const { settings } = useSettingsManager();
    const { startGeneration, isLoading, progress, message, error } = useChapterGeneration();

    const handleTopicSubmit = (topic: string) => {
        if(!user || !settings) {
            console.error("Cannot generate chapter: User or settings are not available.");
            return;
        }

        const request: ChapterGenerationRequest = {
            language: settings.language,
            difficulty: settings.difficulty,
            topic: topic,
            userId: user.id
        };

        startGeneration(request);
    };

    return (
        <div>
            {error && <Alert variant="danger" className="mt-3">Error: {error}</Alert>}
            <StudyBookMascot
                onTopicSubmit={handleTopicSubmit}
                isLoading={isLoading}
                progress={progress}
                message={message}
                celebrationTrigger={celebrationTrigger}
            />
        </div>
    );
}

export default ChapterGenerator;
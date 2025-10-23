import {useStoryBookManager} from "../../features/storyBook/hooks/useStoryBookManager";
import {useShortStoryGeneration} from "../../features/storyBook/shortStoryGeneration/hooks/useShortStoryGeneration";
import Container from "react-bootstrap/Container";
import {Spinner} from "react-bootstrap";
import FlipBook from "../../features/storyBook/components/FlipBook";
import StoryBookMascot from "../../widgets/storyBookMascot/StoryBookMascot";
import React from "react";


const StoryBookPage: React.FC = () => {
    const { title, stories, isLoading: isBookLoading, language, difficulty } = useStoryBookManager();

    const {
        startGeneration,
        isLoading: isGenerating,
        progress,
        message,
    } = useShortStoryGeneration(language, difficulty);


    const isInitialLoading = isBookLoading && stories.length === 0;

    return(
        <div>
            {isInitialLoading ? (
                    <Container className="d-flex justify-content-center align-items-center" style={{minHeight: '80vh'}}>
                        <Spinner animation="border" />
                    </Container>
                ) : (
                    <>
                        <FlipBook
                            stories={stories}
                            title={title}
                        />

                        <StoryBookMascot
                            onTopicSubmit={startGeneration}
                            isLoading={isGenerating}
                            progress={progress}
                            message={message}
                        />
                    </>
                )
            }
        </div>
    );
}

export default StoryBookPage;
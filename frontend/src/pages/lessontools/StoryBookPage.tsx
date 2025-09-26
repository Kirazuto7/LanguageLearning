import {useStoryBookManager} from "../../features/storyBook/hooks/useStoryBookManager";
import {useCallback, useEffect, useState} from "react";
import {useShortStoryGeneration} from "../../features/storyBook/shortStoryGeneration/hooks/useShortStoryGeneration";
import Container from "react-bootstrap/Container";
import {Alert, Spinner} from "react-bootstrap";
import FlipBook from "../../features/storyBook/components/FlipBook";
import StoryBookMascot from "../../widgets/storyBookMascot/StoryBookMascot";


const StoryBookPage: React.FC = () => {
    const { title, stories, isLoading: isBookLoading, language, difficulty } = useStoryBookManager();
    const [navigateToPage, setNavigateToPage] = useState<number | null>(null);

    const {
        startGeneration,
        isLoading: isGenerating,
        progress,
        message,
        error: generationError,
    } = useShortStoryGeneration(language, difficulty);

    const handleFlipComplete = useCallback(() => {
        setNavigateToPage(null);
    }, []);

    const isInitialLoading = isBookLoading && stories.length === 0;

    useEffect(() => {
        if (stories.length > 0) {
            const latestStory = stories[stories.length - 1];
            if (latestStory && latestStory.storyPages.length > 0) {
                const firstPageOfLatestStory = latestStory.storyPages[0];
                setNavigateToPage(firstPageOfLatestStory.pageNumber);
            }
        }
    }, [stories]);

    return(
        <div>
            {isInitialLoading ? (
                    <Container className="d-flex justify-content-center align-items-center" style={{minHeight: '80vh'}}>
                        <Spinner animation="border" />
                    </Container>
                ) : (
                    <>
                        <FlipBook
                            navigateToPage={navigateToPage}
                            onFlipComplete={handleFlipComplete}
                            stories={stories}
                            title={title}
                        />
                        <Container className="mt-4">
                            { generationError && <Alert variant="danger" className="mt-2">{generationError}</Alert>}
                        </Container>
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
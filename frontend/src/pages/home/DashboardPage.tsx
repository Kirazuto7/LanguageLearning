import Container from 'react-bootstrap/Container';
import { useSelector } from 'react-redux';
import { selectCurrentUser } from "../../features/authentication/authSlice";
import React from "react";
import { useDashboardData } from "./useDashboardData";
import FullScreenLoader from "../../shared/components/fullscreenLoader/FullScreenLoader";
import EmptyDashboard from "./components/EmptyDashboard";
import styles from "./components/dashboardpage.module.scss";
import ActionCard from "./components/ActionCard";
import {useNavigate} from "react-router-dom";
import LessonBookIllustration from "./components/subcomponents/LessonBookIllustration";
import StoryBookIllustration from "./components/subcomponents/StoryBookIllustration";

const DashboardPage: React.FC = () => {
  const user = useSelector(selectCurrentUser);
  const { isLoading, lessonBooks, storyBooks, hasData } = useDashboardData();
  const navigate = useNavigate();

  if (isLoading) {
    return <FullScreenLoader/>;
  }

  return (
    <Container fluid className="d-flex flex-column align-items-center flex-grow-1 py-5">
      <div className="glass-container" style={{ width: '90vw', maxWidth: '1600px' }}>
        <h1 className="mb-4 text-center">Welcome {user ? `, ${user.username}` : 'Guest'}!</h1>
        <p className="text-center">Here are your most recent books. Select one to continue your language journey.</p>

        <div className="subtle-divider"/>

        {!hasData ? (
            <EmptyDashboard />
        ) : (
            <>
                {/* Book lists will be rendered here later */}
            </>
        )}

        <div className="subtle-divider"/>

        <div className="glass-container mt-5">
            <h2 className="text-center mb-4">Actions</h2>
            <div className={styles.cardContainer}>
              <ActionCard
                  title="Create a Lesson Book"
                  description="Generate a structured, textbook-style lesson on any topic you choose."
                  buttonText="Start Studying"
                  onButtonClick={() => navigate('/study')}
                  illustration={<LessonBookIllustration />}
              />
              <ActionCard
                  title="Create a Story Book"
                  description="Immerse yourself in an AI-generated story with beautiful illustrations."
                  buttonText="Start Reading"
                  onButtonClick={() => navigate('/read')}
                  illustration={<StoryBookIllustration />}
              />
            </div>
        </div>
      </div>
    </Container>
  );
}
export default DashboardPage;
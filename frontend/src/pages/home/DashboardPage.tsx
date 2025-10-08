import Container from 'react-bootstrap/Container';
import {useSelector} from 'react-redux';
import {selectCurrentUser} from "../../features/authentication/authSlice";
import React, {useState} from "react";
import {useDashboardData} from "./useDashboardData";
import FullScreenLoader from "../../shared/components/fullscreenLoader/FullScreenLoader";
import EmptyDashboard from "./components/EmptyDashboard";
import styles from "./components/dashboardpage.module.scss";
import ActionCard from "./components/ActionCard";
import {useNavigate} from "react-router-dom";
import LessonBookIllustration from "./components/subcomponents/LessonBookIllustration";
import StoryBookIllustration from "./components/subcomponents/StoryBookIllustration";
import HorizontalStack from "../../shared/components/horizontalstack/HorizontalStack";
import LessonBookItem from "./components/LessonBookItem";
import StoryBookItem from "./components/StoryBookItem";
import {BookType} from "../../shared/types/types";
import BookReaderModal from "./components/BookReaderModal";
import {getDifficultyColor, getStoryBookDifficultyColor} from "../../shared/utils/colorUtils";
import {LessonBookLibraryItemDTO, StoryBookLibraryItemDTO} from "../../shared/types/dto";
import {logToServer} from "../../shared/utils/loggingService";

const DashboardPage: React.FC = () => {
  const user = useSelector(selectCurrentUser);
  const { isLoading, lessonBooks, storyBooks, hasData } = useDashboardData();
  const navigate = useNavigate();
  const [selectedBook, setSelectedBook] = useState<{ id: number; type: BookType; color: string; } | null>(null);

  if (isLoading) {
    return <FullScreenLoader/>;
  }

  const handleSelectBook = (book: LessonBookLibraryItemDTO | StoryBookLibraryItemDTO, type: BookType) => {
    let color;
    switch (type) {
        case BookType.LESSON:
            color = getDifficultyColor(book.difficulty);
            break;
        case BookType.STORY:
            color = getStoryBookDifficultyColor(book.difficulty);
            break;
        default:
            color = '';
            break;
    }

    let data = { id: book.id, type, color };
    setSelectedBook(data);
    logToServer('debug', "Selected Book:", data);
  };

  return (
  <>
    <Container fluid className="d-flex flex-column align-items-center flex-grow-1 py-5">
      <div className="glass-container" style={{ width: '90vw', maxWidth: '1600px' }}>
        <h1 className="mb-4 text-center">Welcome {user ? `, ${user.username}` : 'Guest'}!</h1>
        <p className="text-center">Here are your most recent books. Select one to continue your language journey.</p>

        <div className="subtle-divider"/>

        {!hasData ? (
            <EmptyDashboard />
        ) : (
            <>
               <HorizontalStack
                    title="My Lesson Books"
                    width="100%"
                    borderRadius="8px"
               >
                   {lessonBooks.map(book => (
                       <LessonBookItem
                            key={book.id + book.type}
                            book={book}
                            onClick={() => handleSelectBook(book, BookType.LESSON)}
                       />
                   ))}
               </HorizontalStack>

               <HorizontalStack title="My Story Books" width="100%">
                   {storyBooks.map(book => (
                       <StoryBookItem
                            key={book.id + book.type}
                            book={book}
                            onClick={() => handleSelectBook(book, BookType.STORY)}
                       />
                   ))}
               </HorizontalStack>
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

    {selectedBook && (
        <BookReaderModal
            key={selectedBook.id + selectedBook.type}
            show={true}
            onHide={() => setSelectedBook(null)}
            bookId={selectedBook.id}
            bookType={selectedBook.type}
            bookColor={selectedBook.color}
        />
    )}

  </>
  );
}
export default DashboardPage;
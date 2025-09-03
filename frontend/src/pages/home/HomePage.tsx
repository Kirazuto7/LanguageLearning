import Container from 'react-bootstrap/Container';
import Card from 'react-bootstrap/Card';
import { useSelector } from 'react-redux';
import { RootState } from '../../app/store';
import React from "react";

const HomePage: React.FC = () => {

  const { user } = useSelector((state: RootState) => state.auth);

  return (
    <Container fluid className="d-flex flex-column flex-grow-1 py-5">
    <div className={"glass-container mb-4"}>
      <h1 className="mb-4 text-center">Welcome {user ? `, ${user.username}` : 'to LangMaster'}</h1>
      <p className="text-center">Select a tool from the navigation bar above to begin your language journey.</p>
    </div>

    <div className="glass-container mb-4">
      <h2 className="text-center">Language Tools Collection</h2>
      <div className="divider mb-4"/>
      <Card>
          <Card.Header>
              <Card.Title className="text-center fw-bold">Study Your {user?.settings.language} Book</Card.Title>
          </Card.Header>
          <Card.Body>
              <p>This is an interactive book where you can generate lessons on any topic.</p>
              <Card.Subtitle>How to start:</Card.Subtitle>
              <ul>
                  <li>Use the settings gear to pick your language and level.</li>
                  <li>Suggest a topic in the input box below.</li>
                  <li>Press "Send" to generate your custom lesson!</li>
              </ul>
          </Card.Body>
      </Card>
    </div>

  </Container>
  );
}
export default HomePage;
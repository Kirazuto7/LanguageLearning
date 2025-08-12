import styles from '../styles/homepage.module.css';
import Container from 'react-bootstrap/Container';
import Card from 'react-bootstrap/Card';
interface HomePageProps{}
const HomePage: React.FC<HomePageProps> = () => {
  return (
    <Container fluid className="py-5">
    
    <div className={`${styles['glass-container']} mb-4`}>
      <h1 className="text-4xl font-bold mb-4 text-center">Welcome to LangMaster</h1>
      <h5 className="text-lg text-center mb-4">Empower your journey to fluency with immersive tailored lessons and ai-powered language tools.</h5>
      <h3>Ready to Begin?</h3>
      <p>
          Every journey starts with a single step. Here are some tips to make the most of your lessons:
      </p>
      <ul>
          <li>Set a regular study schedule—even 10 minutes a day helps!</li>
          <li>Don’t be afraid to make mistakes. Practice makes progress.</li>
          <li>Try writing your own sentences using new words you learn.</li>
          <li>Speak out loud to improve your pronunciation and confidence.</li>
      </ul>
      <p>
          Let’s make language learning fun and effective together!
      </p>
    </div>

    <div className={`${styles['glass-container']} p-5 mb-4  rounded-3`}>
      <h2 className={`${styles['collection-title']} text-center`}>Language Tools Collection</h2>
      <div className="divider mb-4"/>
      <Card>
          <Card.Header>
              <Card.Title className="text-center fw-bold" style={{cursor: 'pointer'}} onClick={() => window.location.href = '/study'}>Study Book</Card.Title>
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
import { Container, Spinner } from "react-bootstrap";

const FullScreenLoader = () => {
    return (
        <Container
            className="d-flex justify-content-center align-items-center"
            style={{ minHeight: '100vh'}}
        >
            <Spinner animation="border" />
        </Container>
    );
};

export default FullScreenLoader;
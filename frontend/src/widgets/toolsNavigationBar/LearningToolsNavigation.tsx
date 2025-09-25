import styles from './learning-tools-navigation.module.scss';
import Nav from "react-bootstrap/Nav";
import {useSelector} from "react-redux";
import {RootState} from "../../app/store";
import {Link} from "react-router-dom";


const LearningToolsNavigation: React.FC = () => {
    const { user } = useSelector((state: RootState) => state.auth);
    if(user === null) return null; // Do not display if user is not logged in

    return(
        <Nav variant={"pills"}>
            <Nav.Item>
                <Nav.Link as={Link} to={"/study"}>
                    <div className={styles.navLinkContent}>
                        <i className="bi bi-journal-bookmark-fill"/>
                        <span>Lesson Book</span>
                    </div>
                </Nav.Link>
            </Nav.Item>

            <Nav.Item>
                <Nav.Link as={Link} to={"/read"}>
                    <div className={styles.navLinkContent}>
                        <i className="bi bi-book-half"/>
                        <span>Story Book</span>
                    </div>
                </Nav.Link>
            </Nav.Item>
        </Nav>
    );
}

export default LearningToolsNavigation;
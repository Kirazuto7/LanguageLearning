import React from 'react';
import { Container } from "react-bootstrap";
import styles from './emptydashboard.module.scss';

const EmptyDashboard: React.FC = () => {

    return(
        <Container className={styles.emptyDashboardContainer}>
            <div className="text-center mb-5">
                <h4>Start Your Language Journey</h4>
                <p>
                It looks like you haven't created any books yet.
            </p>
            </div>

        </Container>
    );
};

export default EmptyDashboard;
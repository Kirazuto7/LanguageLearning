import React, { useRef, useEffect } from 'react';
import { Button, Collapse } from 'react-bootstrap';
import Styles from '../styles/mascot.module.css';
import { useLanguage } from '../contexts/LanguageContext';

function LanguageSettings({
    openSettings,
    setOpenSettings
}) {
    const { language, setLanguage, difficulty, setDifficulty } = useLanguage();
    const settingsRef = useRef(null);

    useEffect(() => {
        function handleClickOutside(event) {
            if (settingsRef.current && !settingsRef.current.contains(event.target)) {
                setOpenSettings(false);
            }
        }
        // Bind the event listener
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            // Unbind the event listener on clean up
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [settingsRef, setOpenSettings]);

    return (
        <div className={Styles.settingsContainer} ref={settingsRef}>
            <Button
                className={Styles.settingsButton}
                onClick={() => setOpenSettings(!openSettings)}
                aria-controls="chapter-settings-collapse"
                aria-expanded={openSettings}
                size="sm"
            >
                <i className="bi bi-gear-fill"></i>
            </Button>

            <Collapse in={openSettings}>
                <div id="chapter-settings-collapse" className={Styles.settingsPanel}>
                    <div className="row justify-content-center">
                        <div className="col-auto">
                            <div className={`card ${Styles.settingsCard}`} style={{ width: '300px' }}>
                                <div className="card-header">Language Settings</div>
                                <div className="card-body">
                                    <div className="mb-3">
                                        <label htmlFor="language-select" className="form-label">Language:</label>
                                        <select id="language-select" className="form-select" value={language} onChange={(e) => setLanguage(e.target.value)}>
                                            <option value="Korean">Korean</option>
                                            <option value="Japanese">Japanese</option>
                                        </select>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="level-select" className="form-label">Proficiency Level:</label>
                                        <select id="level-select" className="form-select" value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
                                            <option value="Beginner">Beginner</option>
                                            <option value="Pre-Intermediate">Pre-Intermediate</option>
                                            <option value="Intermediate">Intermediate</option>
                                            <option value="Advanced">Advanced</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </Collapse>
        </div>
    );
}

export default LanguageSettings;

import React, { useRef, useEffect } from 'react';
import { Collapse } from 'react-bootstrap';
import styles from './settings.module.scss';
import { useSettingsManager } from '../hooks/useSettingsManager';
import { themes, languages, difficulties, mascots } from "../../../shared/types/options";
import { MascotName } from "../../../shared/types/types";
import { useAppSelector } from "../../../app/hooks";
import { selectIsAnyGenerationLoading } from "../../../widgets/progressBar/progressSlice";

interface SettingsProps {
    openSettings: boolean;
    setOpenSettings: (open: boolean) => void;
}

const Settings: React.FC<SettingsProps> = ({
    openSettings,
    setOpenSettings
}) => {
    const { settings, updateSettings, isLoading: isUpdatingSettings } = useSettingsManager();
    const isGenerationLoading = useAppSelector(selectIsAnyGenerationLoading);
    const settingsRef = useRef<HTMLDivElement>(null);

    // Only language and difficulty are disabled during generation
    const isGenerationDisabled = isUpdatingSettings || isGenerationLoading;

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (settingsRef.current && !settingsRef.current.contains(event.target as Node)) {
                setOpenSettings(false);
            }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [setOpenSettings]);

    return (
        <div className={styles.settingsContainer} ref={settingsRef}>
            <button
                className={styles.settingsButton}
                onClick={() => setOpenSettings(!openSettings)}
                aria-controls="settings-collapse"
                aria-expanded={openSettings}
            >
                <div className={styles.navLinkContent}>
                    <i className="bi bi-gear-fill"></i>
                    <span>Settings</span>
                </div>
            </button>

            <Collapse in={openSettings}>
                <div id="settings-collapse" className={styles.settingsPanel}>
                    <div className="row justify-content-center">
                        <div className="col-auto">
                            <div className={`card ${styles.settingsCard}`} style={{ width: '300px' }}>
                                <div className="card-header">Settings</div>
                                <div className="card-body">
                                    <div className="mb-3">
                                        <label htmlFor="language-select" className="form-label">
                                            Language:
                                        </label>
                                        <select
                                            id="language-select"
                                            className="form-select"
                                            value={settings?.language || ''}
                                            onChange={(e) => updateSettings({ language: e.target.value })}
                                            disabled={isGenerationDisabled}
                                        >
                                            {languages.map(lang => (
                                                <option key={lang.value} value={lang.value}>{lang.label}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="level-select" className="form-label">
                                            Proficiency Level:
                                        </label>
                                        <select
                                            id="level-select"
                                            className="form-select"
                                            value={settings?.difficulty || ''}
                                            onChange={(e) => updateSettings({ difficulty: e.target.value })}
                                            disabled={isGenerationDisabled}
                                        >
                                            {difficulties.map(diff => (
                                                <option key={diff.value} value={diff.value}>{diff.label}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="theme-select" className="form-label">
                                            Theme:
                                        </label>
                                        <select
                                            id="theme-select"
                                            className="form-select"
                                            value={settings?.theme || ''}
                                            onChange={(e) => updateSettings({ theme: e.target.value })}
                                            disabled={isUpdatingSettings}
                                        >
                                            {themes.map(theme => (
                                                <option key={theme.value} value={theme.value}>{theme.label}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="mascot-select" className="form-label">
                                            Assistant:
                                        </label>
                                        <select
                                            id="mascot-select"
                                            className="form-select"
                                            value={settings?.mascot || ''}
                                            onChange={(e) => updateSettings({ mascot: e.target.value as MascotName })}
                                            disabled={isUpdatingSettings}
                                        >
                                            {mascots.map(mascot => (
                                                <option key={mascot.value} value={mascot.value}>{mascot.label}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="auto-speak-toggle" className="col-sm-4 col-form-label">
                                            Auto-Speak
                                        </label>
                                        <div className="col-sm-8 d-flex align-items-center">
                                            <div className="form-check form-switch">
                                                <input
                                                    className="form-check-input"
                                                    type="checkbox"
                                                    role="switch"
                                                    id="auto-speak-toggle"
                                                    checked={settings?.autoSpeakEnabled ?? true}
                                                    onChange={(e) => updateSettings({ autoSpeakEnabled: e.target.checked})}
                                                    disabled={isUpdatingSettings}
                                                />
                                            </div>
                                        </div>
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

export default Settings;

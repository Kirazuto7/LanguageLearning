import { Modal, Button, Form, Spinner, Alert } from "react-bootstrap";
import { useState, useEffect } from "react";
import { useTranslate } from "../hooks/useTranslate";
import { useSettingsManager } from "../../../features/userSettings/hooks/useSettingsManager";
import { formatText } from "../../../shared/utils/textUtils";
import { filterInputByLanguage } from "../../../shared/utils/languageValidation";
import styles from './translation.module.scss';
import { Clipboard, ClipboardCheck } from "react-bootstrap-icons";

interface TranslationModalProps {
    show: boolean;
    onHide: () => void;
}

export const TranslationModal = ({ show, onHide }: TranslationModalProps) => {
    const [textToTranslate, setTextToTranslate] = useState<string>('');
    const [isCopied, setIsCopied] = useState<boolean>(false);
    const { translate, isLoading, isError, data, reset } = useTranslate();
    const { settings } = useSettingsManager();
    const sourceLanguage = settings?.language || 'the selected language';

    const handleTranslate = async () => {
        if (!textToTranslate.trim()) return;

        // Catch block to prevent an unhandled promise rejection
        await translate(textToTranslate).catch(() => {});
    };

    const handleCopy = () => {
        if (data?.translatedText) {
            navigator.clipboard.writeText(data.translatedText);
            setIsCopied(true);
            setTimeout(() => setIsCopied(false), 2000);
        }
    };

    useEffect(() => {
        if (!show) {
            setTextToTranslate('');
            setIsCopied(false);
            reset();
        }
    }, [show, reset]);

    return (
        <Modal show={show} onHide={onHide} size="lg" centered contentClassName={styles['themed-modal-content']}>
            <Modal.Header closeButton>
                <Modal.Title>Translate {formatText(sourceLanguage)} Text</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form.Group>
                    <Form.Label>Enter {formatText(sourceLanguage)} text to translate...</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={5}
                        value={textToTranslate}
                        onChange={(e) => setTextToTranslate(filterInputByLanguage(e.target.value, settings?.language))}
                    />
                </Form.Group>
                <div className={styles['translation-result-box']}>
                    {isLoading ? (
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                    ) : isError ? (
                        <Alert variant="danger" className="w-100 text-center m-0">
                            Translation failed. Please try again.
                        </Alert>
                    ) : (
                        <>
                            {data?.translatedText && (
                                <Button
                                    variant="light"
                                    size="sm"
                                    className={styles['copy-button']}
                                    onClick={handleCopy}
                                >
                                    {isCopied ? <ClipboardCheck/> : <Clipboard/>}
                                </Button>
                            )}
                            <p>{data?.translatedText || 'Translation will appear here...'}</p>
                        </>
                    )}
                </div>
            </Modal.Body>
            <Modal.Footer className={styles['modal-footer']}>
                <Button variant="secondary" onClick={onHide}>
                    Close
                </Button>
                <Button
                    variant="primary"
                    onClick={handleTranslate}
                    disabled={isLoading || !textToTranslate.trim()}
                >
                    {isLoading ? 'Translating...' : 'Translate'}
                </Button>
            </Modal.Footer>
        </Modal>
    );
};
import React, { createContext, useState, useCallback, ReactNode, useContext } from 'react';
import AlertMessage from '../components/alert/AlertMessage';
import { AlertLevel } from "../types/types";

interface AlertState {
    message: string;
    level: AlertLevel;
}

interface AlertContextType {
    showAlert: (message: string, level: AlertLevel) => void;
}

const AlertContext = createContext<AlertContextType | undefined>(undefined);

export const AlertProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [alert, setAlert] = useState<AlertState | null>(null);

    const showAlert = useCallback((message: string, level: AlertLevel) => {
        setAlert({ message, level });
    }, []);

    const handleClose = () => setAlert(null);

    return (
        <AlertContext.Provider value={{ showAlert }}>
            {children}
            {alert && <AlertMessage message={alert.message} level={alert.level} timeout={3000} onClose={handleClose} />}
        </AlertContext.Provider>
    );
};

export const useAlert = () => {
    const context = useContext(AlertContext);
    if (!context) throw new Error('useAlert must be used within an AlertProvider');
    return context;
};
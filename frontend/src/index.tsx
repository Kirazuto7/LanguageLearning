import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './index.css';
import App from './app/App';
import reportWebVitals from './reportWebVitals';
import { store } from './app/store';
import { Provider } from 'react-redux';
import {ThemeProvider} from './shared/contexts/ThemeContext';

const rootElement = document.getElementById('root');
if(!rootElement) {
    throw new Error("Could not find the root element to mount the app.");
}

const root = ReactDOM.createRoot(rootElement);
root.render(
    <React.StrictMode>
        {/*
            Enable React Router v7 future flags to opt-in to new behaviors early.StrictMode
            - v7_startTransition: Wraps state updates in React.startTransition for better UI responsiveness.
            - v7_relativeSplatPath: Changes how relative paths are resolved in splat routes.
        */}
        <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
            <Provider store={store}>
                <ThemeProvider>
                    <App />
                </ThemeProvider>
            </Provider>
        </BrowserRouter>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

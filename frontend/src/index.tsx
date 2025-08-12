import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { LanguageSettingsProvider } from './contexts/LanguageSettingsContext';
import { BookProvider } from './contexts/BookContext';

const rootElement = document.getElementById('root');
if(!rootElement) {
    throw new Error("Could not find the root element to mount the app.");
}

const root = ReactDOM.createRoot(rootElement);
root.render(
    <React.StrictMode>
        <BrowserRouter>
          <LanguageSettingsProvider>
            <BookProvider>
              <App />
            </BookProvider>
          </LanguageSettingsProvider>
        </BrowserRouter>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

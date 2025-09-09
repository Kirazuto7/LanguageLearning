/**
 * Sends a log message to the frontend dev server to be printed in the Docker logs.
 * This is a "fire-and-forget" operation.
 * @param level The log level (e.g., 'info', 'warn', 'error').
 * @param message The primary log message.
 * @param context Optional additional data to log.
 */
 export const logToServer = (level: 'info' | 'warn' | 'error', message: string, context?: any): void => {
    fetch('/api/logs/client', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ level, message, context }),
    }).catch(err => {
        console.error("Failed to send log to server:", err);
    });
 };

 export const toString = (object: any) => {
    return JSON.stringify(object, null, 2);
 }
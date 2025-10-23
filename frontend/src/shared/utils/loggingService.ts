/**
 * Sends a log message to the frontend dev server to be printed in the Docker logs.
 * This is a "fire-and-forget" operation.
 * @param level The log level (e.g., 'info', 'warn', 'error').
 * @param message The primary log message.
 * @param context Optional additional data to log.
 */
 export const logToServer = (level: 'info' | 'debug' |'warn' | 'error', message: string, context?: any): void => {
    const timestamp = new Date().toISOString();
    const messageWithTimestamp = `[Client:${timestamp}] ${message}`;
    fetch('/api/logs/client', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ level, message: messageWithTimestamp, context }),
    }).catch(err => {
        console.error("Failed to send log to server:", err);
    });
 };

 export const toString = (object: any) => {
    return JSON.stringify(object, null, 2);
 }

/**
 * Safely converts an object to a JSON string, handling circular references.
 * @param object The object to convert.
 * @returns A JSON string representation of the object.
 */
export const safeToString = (object: any): string => {
    const cache = new Set();
    return JSON.stringify(
        object,
        (key, value) => {
            if (typeof value === 'object' && value !== null) {
                if (cache.has(value)) {
                    // Circular reference found, discard key
                    return '[Circular]';
                }
                // Store value in our collection
                cache.add(value);
            }
            return value;
        },
        2
    );
};
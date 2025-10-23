import { createClient, Client } from "graphql-ws";
import {logToServer} from "../../shared/utils/loggingService";

let wsClient: Client | null = null;

const getWsClient = (): Client => {
    if (!wsClient) {
        wsClient = createClient({
            url: `${window.location.protocol === "https:" ? "wss:" : "ws:"}//${window.location.host}/graphql`,
            // Expect a keep-alive message every 25 seconds. If not received, the client will close and retry.
            keepAlive: 25_000,
            retryAttempts: Infinity,
            retryWait: async (retries) => {
                const delay = Math.min(1000 * retries, 5000);
                logToServer('debug', `GraphQL WS retry attempt #${retries}, waiting ${delay}ms`);
                await new Promise((resolve) => setTimeout(resolve, delay));
            },
            shouldRetry: () => true,
        });
    }
    return wsClient;
};

export function subscribe<T = any>(
    query: string,
    variables: Record<string, any>,
    onNext: (data: T) => void,
    onError: (err: any) => void,
    onComplete: () => void
) {
    return getWsClient().subscribe<T>(
        { query, variables },
        {
            next: ({ data }) => onNext(data as T),
            error: onError,
            complete: onComplete,
        }
    );
}

export function closeWsClient() {
    if (wsClient) {
        wsClient.dispose();
        wsClient = null;
    }
}
import { createClient, Client } from "graphql-ws";

let wsClient: Client | null = null;

const getWsClient = (): Client => {
    if (!wsClient) {
        wsClient = createClient({
            url: `${window.location.protocol === "https:" ? "wss:" : "ws:"}//${window.location.host}/graphql`,
            // Expect a keep-alive message every 25 seconds. If not received, the client will close and retry.
            keepAlive: 25_000,
            retryAttempts: 5,
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
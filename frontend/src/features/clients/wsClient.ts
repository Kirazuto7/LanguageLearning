import { createClient, Client } from "graphql-ws";

const wsProtocol = window.location.protocol === "https:" ? "wss:" : "ws:";

const wsClient: Client = createClient({
    url: `${wsProtocol}//${window.location.host}/graphql`,
    webSocketImpl: WebSocket,
});

export function subscribe<T = any>(
    query: string,
    variables: Record<string, any>,
    onNext: (data: T) => void,
    onError: (err: any) => void,
    onComplete: () => void
) {
    return wsClient.subscribe<T>(
        { query, variables },
        {
            next: ({ data }) => onNext(data as T),
            error: onError,
            complete: onComplete,
        }
    );
}
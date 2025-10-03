import { createClient, Client } from "graphql-ws";

const wsClient: Client = createClient({
    url: `${window.location.protocol === "https:" ? "wss:" : "ws:"}//${window.location.host}/graphql`,
    retryAttempts: 5,
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
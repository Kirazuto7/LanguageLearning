import { createClient, Client } from "graphql-sse";

let sseClient: Client | null = null;

const getSseClient = (): Client => {
    if (!sseClient) {
        sseClient = createClient({
            url: `${window.location.protocol}//${window.location.host}/graphql`,
            credentials: 'include',
            headers: {
                'Accept': 'text/event-stream'
            },
        });
    }
    return sseClient;
};

export function subscribeSSE<T = any>(
    query: string,
    variables: Record<string, any>,
    onNext: (data: T) => void,
    onError: (err: any) => void,
    onComplete: () => void
) {
    return getSseClient().subscribe<T>(
        { query, variables },
        {
            next: ( { data }) => onNext(data as T),
            error: onError,
            complete: onComplete,
        }
    );
}

export function closeSseClient() {
    if (sseClient) {
        sseClient.dispose();
        sseClient = null;
    }
}

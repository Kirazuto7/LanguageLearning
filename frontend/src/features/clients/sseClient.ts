import { createClient, Client } from "graphql-sse";

const sseClient: Client = createClient({
    url: `${window.location.protocol}//${window.location.host}/graphql`,
    credentials: 'include',
    headers: {
        'Accept': 'text/event-stream'
    },
});

export function subscribeSSE<T = any>(
    query: string,
    variables: Record<string, any>,
    onNext: (data: T) => void,
    onError: (err: any) => void,
    onComplete: () => void
) {
    return sseClient.subscribe<T>(
        { query, variables },
        {
            next: ( { data }) => onNext(data as T),
            error: onError,
            complete: onComplete,
        }
    );
}

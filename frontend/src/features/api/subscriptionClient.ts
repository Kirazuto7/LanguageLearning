import { createClient, Client } from "graphql-ws";
import { logToServer } from "../../utils/loggingService";

const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
const client: Client = createClient({
    url: `${wsProtocol}//${window.location.host}/graphql`,
});

interface SubscriptionPayload<T> {
    query: string;
    variables: Record<string, any>;
    transformResponse: (data: any) => T;
}

/**
 * A reusable async generator function that wraps the graphql-ws client.
 * @param payload The subscription query, variables, and a function to transform the response.
 */
 export async function* createSubscription<T>(payload: SubscriptionPayload<T>): AsyncGenerator<T> {
    const { query, variables, transformResponse } = payload;
    let unsubscribe = () => {};

    const pending: T[] = [];
    let deferred: { resolve: (value: IteratorResult<T>) => void; reject: (reason?: any) => void; } | null = null;

    try {
        unsubscribe = client.subscribe(
            { query, variables },
            {
                next: (data) => {
                    const transformed = transformResponse(data);
                    if (deferred) {
                        deferred.resolve({ value: transformed, done: false });
                        deferred = null;
                    }
                    else {
                        pending.push(transformed);
                    }
                },
                error: (err) => {
                    logToServer('error', 'GraphQL Subscription Error', { error: err });
                    deferred?.reject(err);
                },
                complete: () => deferred?.resolve({ value: undefined, done: true }),
            }
        );

        while (true) {
            if (pending.length > 0) {
                yield pending.shift()!;
            }
            else {
                const promise = new Promise<IteratorResult<T>>((resolve, reject) => deferred = { resolve, reject });
                const result = await promise;
                if (result.done) break;
                yield result.value;
            }
        }

    }
    finally {
        unsubscribe();
    }
 }
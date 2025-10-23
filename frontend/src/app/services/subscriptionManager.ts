import {subscribe} from "./wsClient";
import {logToServer} from "../../shared/utils/loggingService";

/**
 * Defines options for a GraphQL subscription.
 */
export interface SubscriptionOptions<T> {
    query: string;
    variables?: Record<string, any>;
    onNext: (data: T) => void;
    onError?: (error: any) => void;
    onComplete?: () => void; // Called ONLY when the underlying WebSocket stream closes.
}

type ActiveSub = {
    unsubscribe: () => void;
    taskId: string;
}

// Map containing the active unsubscribe function for a task
const activeSubs = new Map<string, ActiveSub>();

/**
 * Starts a new GraphQL subscription and manges its lifecycle.
 * @param subscriptionId A unique identifier for this subscription instance.
 * @param options The subscription query, variables, and data handling callbacks.
 */
export function startSubscription<T = any>(subscriptionId: string, options: SubscriptionOptions<T>) {
    if (activeSubs.has(subscriptionId)) {
        logToServer('warn', "Subscription already active for this ID.", { subscriptionId });
        return;
    }

    logToServer('info', `Starting WebSocket subscription for ${subscriptionId}`);

    const { query, variables = {}, onNext, onError, onComplete } = options;

    const unsubscribe = subscribe<T>(
        query,
        variables,
        (data) => { // onNext
            onNext(data);
        },
        (err) => { // onError
            logToServer("error", "onError: WS error", { error: err, subscriptionId });
            if (onError) onError(err);
        },
        () => { // onComplete (WebSocket stream closed)
            logToServer("warn", `onComplete: WS stream for ${subscriptionId} closed.`, { subscriptionId });
            if (onComplete) onComplete();
        }
    );

    activeSubs.set(subscriptionId, { unsubscribe, taskId: subscriptionId });
}

/**
 * Stops a specific subscription and removes it from the active subscription map.
 * @param subscriptionId The unique identifier for the subscription to stop.
 */
export function stopSubscription(subscriptionId: string) {
    const sub = activeSubs.get(subscriptionId);
    if (sub) {
        sub.unsubscribe();
        activeSubs.delete(subscriptionId);
        logToServer("info", "Unsubscribed and cleaned up task.", { subscriptionId });
    }
}
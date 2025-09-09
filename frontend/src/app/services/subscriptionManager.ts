import { subscribe } from "./wsClient";
import { logToServer } from "../../shared/utils/loggingService";

/**
 * Defines options for a GraphQL subscription.
 */
export interface SubscriptionOptions<T> {
    query: string;
    variables?: Record<string, any>;
    onNext: (data: T) => void;
    onError?: (error: any) => void;
    onComplete?: () => void;
}

type ActiveSub = {
    unsubscribe: () => void;
    taskId: string;
}

// Map containing the active subscriptions for a task
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

    const { query, variables = {}, onNext , onError, onComplete } = options;

    const unsubscribe = subscribe<T>(
        query,
        variables,
        onNext, // onNext
        (err) => { // onError
            logToServer("error", "WS error", { error: err, subscriptionId });
            if (onError) onError(err);
            cleanup(subscriptionId);
        },
        () => { // onComplete
            logToServer("info", "WS stream from server has completed.", { subscriptionId });
            if (onComplete) onComplete();
            cleanup(subscriptionId);
        }
    );

    activeSubs.set(subscriptionId, { unsubscribe, taskId: subscriptionId });
}

/**
 * Stops a specific subscription and removes it from the active subscription map.
 * @param subscriptionId The unique identifier for the subscription to stop.
 */
export function stopSubscription(subscriptionId: string) {
    const subscription = activeSubs.get(subscriptionId);
    if (subscription) {
        subscription.unsubscribe();
        activeSubs.delete(subscriptionId);
        logToServer("info", "Unsubscribed and cleaned up task.", { subscriptionId });
    }
}

/**
 * Cleans up the subscription and associated state after completion.
 * @param subscriptionId The unique identifier for the subscription.
 */
function cleanup(subscriptionId: string) {
    stopSubscription(subscriptionId);
}
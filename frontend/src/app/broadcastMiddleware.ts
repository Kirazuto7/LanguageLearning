import { Middleware, Store, UnknownAction } from "@reduxjs/toolkit";
import { logOut } from "../features/authentication/authSlice";
import { userApiSlice } from "../shared/api/userApiSlice";
import { syncSettings } from "../features/userSettings/settingsSlice";

interface BroadcastAction {
    type: string;
    payload?: unknown;
    meta?: { fromBroadcast?: boolean };
}

// Create a BroadcastChannel to communicate between tabs.
const channel = new BroadcastChannel('redux_state_sync');

export const broadcastMiddleware: Middleware = store => next => (action: unknown) => {

    if (typeof action === 'object' && action !== null) {
        if ('meta' in action && (action as BroadcastAction).meta?.fromBroadcast) {
            return next(action);
        }

        if (userApiSlice.endpoints.logout.matchFulfilled(action)) {
            // ...post a message to all other tabs to trigger a logout there.
            channel.postMessage({ type: 'LOGOUT' });
        }

        if (userApiSlice.endpoints.login.matchFulfilled(action as any) || userApiSlice.endpoints.register.matchFulfilled(action as any)) {
            channel.postMessage({ type: 'LOGIN' });
        }

        if (userApiSlice.endpoints.updateSettings.matchFulfilled(action as any)) {
            channel.postMessage(syncSettings((action as any).payload));
        }

        if ('type' in action && typeof (action as {type: string }).type === 'string') {
            if ((action as { type: string }).type.startsWith('progress/')) {
                channel.postMessage(action);
            }
        }
    }


    return next(action);
};

// This function should be called once when the store is initialized.
export const subscribeToBroadcast = (store: Store) => {
    channel.onmessage = (event) => {
        const action = event.data as BroadcastAction;

        if (action.type === 'LOGOUT') {
            // When a 'LOGOUT' message is received from another tab, dispatch the logOut action in this tab.
            store.dispatch(logOut());
            return;
        }

        if (action.type === 'LOGIN') {
            // When a 'LOGIN' message is received, reload the page to sync the new session.
            window.location.reload();
            return;
        }

        if (action.type && action.payload !== undefined) {
            store.dispatch({ ...action, meta: { fromBroadcast: true }});
        }
    };
};
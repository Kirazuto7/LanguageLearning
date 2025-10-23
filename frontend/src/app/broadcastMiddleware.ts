import { Middleware, Store, UnknownAction } from "@reduxjs/toolkit";
import { logOut } from "../features/authentication/authSlice";
import { userApiSlice } from "../shared/api/userApiSlice";
import { syncSettings } from "../features/userSettings/settingsSlice";
import { graphqlApiSlice } from "../shared/api/graphqlApiSlice";
import { apiSlice } from "../shared/api/apiSlice";
import { authApiSlice } from "../shared/api/authApiSlice";

interface BroadcastAction {
    type: string;
    payload?: unknown;
    meta?: { fromBroadcast?: boolean };
}

// Create a BroadcastChannel to communicate between tabs.
const channel = new BroadcastChannel('redux_state_sync');

export const broadcastMiddleware: Middleware = store => next => (action: unknown) => {

    if (typeof action === 'object' && action !== null) {
        // If the action came from a broadcast, don't rebroadcast it.
        if ('meta' in action && (action as BroadcastAction).meta?.fromBroadcast) {
            return next(action);
        }

        // When a logout is successful in one tab, tell other tabs to log out.
        if (authApiSlice.endpoints.logout.matchFulfilled(action)) {
            channel.postMessage({ type: 'LOGOUT' });
        }

        // When a login or register is successful, tell other tabs to re-sync.
        if (authApiSlice.endpoints.login.matchFulfilled(action as any) || authApiSlice.endpoints.register.matchFulfilled(action as any)) {
            channel.postMessage({ type: 'LOGIN_SUCCESS' });
        }

        // When settings are updated, broadcast the new settings object.
        if (userApiSlice.endpoints.updateSettings.matchFulfilled(action as any)) {
            channel.postMessage(syncSettings((action as any).payload));
        }

        // When a progress action is dispatched, broadcast it to other tabs.
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

        switch (action.type) {
            case 'LOGOUT':
                store.dispatch(logOut());
                store.dispatch(apiSlice.util.resetApiState());
                store.dispatch(graphqlApiSlice.util.resetApiState());
                break;
            case 'LOGIN_SUCCESS':
                // Reset the API state to force a re-fetch of user data.
                store.dispatch(apiSlice.util.resetApiState());
                store.dispatch(graphqlApiSlice.util.resetApiState());
                break;
            default:
                // For any other action (e.g., settings/progress sync), dispatch it directly
                if (action.type) {
                    store.dispatch({ ...action, meta: { fromBroadcast: true }});
                }
                break;
        }
    };
};
import {BaseQueryFn, FetchArgs, fetchBaseQuery, FetchBaseQueryError} from "@reduxjs/toolkit/query/react";
import {logToServer} from "../utils/loggingService";
import { authApiSlice } from "./authApiSlice";
import { logOut } from "../../features/authentication/authSlice";
import {Mutex} from "async-mutex";
import {baseQuery} from "./baseQuery";


// A mutex to ensure only one token refresh is in progress at any time.
const mutex = new Mutex();

export const baseQueryWithReauth: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
    const url = typeof args === 'string' ? args : args.url;

    // Define endpoints that should not trigger a token refresh.
    const nonAuthEndpoints = ['/users/refresh', '/users/health', '/logs/client', '/users/login', '/users/register', '/users/complete-oidc-registration'];
    const isNonAuthEndpoint = nonAuthEndpoints.some(endpoint => url.includes(endpoint));

    // If the endpoint is a non-authentication endpoint, bypass the re-authentication logic entirely.
    if (isNonAuthEndpoint) {
        logToServer('debug', `Skipping re-auth for non-authentication endpoint: ${url}`);
        return baseQuery(args, api, extraOptions);
    }

    // Wait for any ongoing token refresh to complete before making the initial request.
    await mutex.waitForUnlock();

    logToServer('debug', 'baseQueryWithReauth: Making initial request.', { args });
    let result = await baseQuery(args, api, extraOptions);

    if (result.error && (result.error.status === 401 || result.error.status === 403)) {
        logToServer('warn', 'baseQueryWithReauth: Initial request failed with auth error. Attempting refresh.', { error: result.error });

        // The first request to fail will acquire the mutex and attempt to refresh the token.
        if (!mutex.isLocked()) {
            const release = await mutex.acquire();
            try {
                logToServer('debug', "baseQueryWithReauth: Acquired mutex. Attempting token refresh.");
                await api.dispatch(authApiSlice.endpoints.refreshToken.initiate()).unwrap();
                logToServer('debug', 'baseQueryWithReauth: Token refresh successful. Retrying original request.');
            }
            catch (refreshError) {
                logToServer('error', 'baseQueryWithReauth: Token refresh failed. Logging out user.', { refreshError });
                api.dispatch(logOut());
                // After a failed refresh, we can return the original error to avoid a pointless retry.
                return result;
            }
            finally {
                release();
                logToServer('debug', 'baseQueryWithReauth: Releasing mutex.');
            }
        }

        // All requests (the original that initiated the refresh and any subsequent ones that were waiting)
        // will retry the request after the mutex is released.
        logToServer('debug', 'baseQueryWithReauth: Retrying original request after mutex release.');
        result = await baseQuery(args, api, extraOptions);
    }

    return result;
};
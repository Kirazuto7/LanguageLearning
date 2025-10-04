import {BaseQueryFn, FetchArgs, fetchBaseQuery, FetchBaseQueryError} from "@reduxjs/toolkit/query/react";
import {logToServer} from "../utils/loggingService";
import {userApiSlice} from "./userApiSlice";
import { logOut } from "../../features/authentication/authSlice";
import {Mutex} from "async-mutex";

const baseQuery = fetchBaseQuery({ baseUrl: '/api'});

// A mutex to ensure only one token refresh is in progress at any time.
const mutex = new Mutex();

export const baseQueryWithReauth: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
    await mutex.waitForUnlock();
    logToServer('debug', 'baseQueryWithReauth: Making initial request.', { args });

    let result = await baseQuery(args, api, extraOptions);

    // Do not retry for 'refreshToken' endpoint to avoid infinite loops
    if (typeof args !== 'string' && args.url.includes('/users/refresh')) {
        logToServer('debug', 'Skipping re-auth for refresh token endpoint.');
        return result;
    }

    if (result.error && (result.error.status === 401 || result.error.status === 403)) {
        logToServer('warn', 'baseQueryWithReauth: Initial request failed with auth error. Attempting refresh.', { error: result.error });
        if (!mutex.isLocked()) {
            const release = await mutex.acquire();

            try {
                logToServer('debug', "baseQueryWithReauth: Acquired mutex. Attempting token refresh.");
                await api.dispatch(userApiSlice.endpoints.refreshToken.initiate()).unwrap();
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
                logToServer('info', 'baseQueryWithReauth: Releasing mutex.');
            }
        }
        // Retry the request after the mutex has been released.
        result = await baseQuery(args, api, extraOptions);
    }
    return result;
};
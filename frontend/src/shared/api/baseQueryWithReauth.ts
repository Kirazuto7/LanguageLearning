import {BaseQueryFn, FetchArgs, fetchBaseQuery, FetchBaseQueryError} from "@reduxjs/toolkit/query/react";
import {logToServer} from "../utils/loggingService";
import {userApiSlice} from "./userApiSlice";
import { logOut } from "../../features/authentication/authSlice";

const baseQuery = fetchBaseQuery({ baseUrl: '/api'});

export const baseQueryWithReauth: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
    logToServer('debug', 'baseQueryWithReauth: Making initial request.', { args });

    let result = await baseQuery(args, api, extraOptions);

    // Do not retry for 'refreshToken' endpoint to avoid infinite loops
    if (typeof args !== 'string' && args.url.includes('/users/refresh')) {
        logToServer('debug', 'Skipping re-auth for refresh token endpoint.');
        return result;
    }

    if (result.error && (result.error.status === 401 || result.error.status === 403)) {
        logToServer('warn', 'baseQueryWithReauth: Initial request failed with auth error. Attempting refresh.', { error: result.error });

        try {
            await api.dispatch(userApiSlice.endpoints.refreshToken.initiate()).unwrap();
            logToServer('info', 'baseQueryWithReauth: Token refresh successful. Retrying original request.');
            result = await baseQuery(args, api, extraOptions);
            logToServer('debug', 'baseQueryWithReauth: Retry attempt result.', { retryResult: result });
        }
        catch (refreshError) {
            logToServer('error', 'baseQueryWithReauth: Token refresh failed. Logging out user.', { refreshError });
            api.dispatch(logOut());
        }
    }
    return result;
};
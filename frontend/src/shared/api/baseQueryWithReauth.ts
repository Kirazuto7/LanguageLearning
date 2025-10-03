import {BaseQueryFn, FetchArgs, fetchBaseQuery, FetchBaseQueryError} from "@reduxjs/toolkit/query/react";
import {logToServer} from "../utils/loggingService";
import {logOut} from "../../features/authentication/authSlice";
import {userApiSlice} from "./userApiSlice";

const baseQuery = fetchBaseQuery({ baseUrl: '/api'});

export const baseQueryWithReauth: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
    let result = await baseQuery(args, api, extraOptions);

    if (result.error && (result.error.status === 401 || result.error.status === 403)) {
        logToServer('info', 'Received auth error. Attempting to refresh token.');

        try {
            await api.dispatch(userApiSlice.endpoints.refreshToken.initiate()).unwrap();
            logToServer('info', 'Token refresh successful. Retrying original request.');
            result = await baseQuery(args, api, extraOptions);
        }
        catch (refreshError) {
            logToServer('error', 'Token refresh failed. Logging out user.', { error: refreshError });
            api.dispatch(logOut());
        }
    }
    return result;
};
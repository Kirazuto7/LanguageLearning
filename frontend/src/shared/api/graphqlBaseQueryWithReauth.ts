import {ClientError, GraphQLClient} from "graphql-request";
import {BaseQueryFn} from "@reduxjs/toolkit/query/react";
import {logToServer, toString} from "../utils/loggingService";
import {logOut} from "../../features/authentication/authSlice";
import {authApiSlice} from "./authApiSlice";
import {Mutex} from "async-mutex";

interface GraphQlQueryError {
    status: number;
    data: any;
}

const client = new GraphQLClient(`${window.location.origin}/graphql`, {
    credentials: 'include',
});

const mutex = new Mutex();

const graphqlBaseQuery: BaseQueryFn<
    { body: string; variables?: any },
    unknown,
    GraphQlQueryError
> = async ({ body, variables }, { dispatch }) => {
    try {
        const result = await client.request(body, variables);
        return { data: result };
    }
    catch (error) {
        if (error instanceof ClientError) {
            logToServer('error', "Client error occurred.", toString(error.response.errors));
            return { error: { status: error.response.status, data: error.response.errors } };
        }
        return { error: { status: 500, data: 'An unknown server error occurred' } };
    }
};

export const graphqlBaseQueryWithReauth: BaseQueryFn<
    { body: string; variables?: any },
    unknown,
    unknown
> = async(args, api, extraOptions) => {
    await mutex.waitForUnlock();
    let result = await graphqlBaseQuery(args, api, extraOptions);

    if (result.error) {
        const hasAuthError = Array.isArray(result.error?.data) && result.error.data.some(
            (e: any) => e.extensions?.classification === 'UNAUTHORIZED' ||
                         e.extensions?.classification === 'FORBIDDEN' ||
                         e.message.includes('Access Denied')
        );
        if (hasAuthError) {
            if (!mutex.isLocked()) {
                const release = await mutex.acquire();

                try {
                    logToServer('debug', 'graphqlBaseQueryWithReauth: Acquired mutex. Attempting to refresh token.');
                    await api.dispatch(authApiSlice.endpoints.refreshToken.initiate()).unwrap();
                    logToServer('debug', 'Token refresh successful. Retrying original GraphQL request.');
                }
                catch (refreshError: any) {
                    // The error object from `unwrap()` contains status and data properties.
                    // If the refresh token fails with 401, it means the session is truly expired.
                    if (refreshError.status === 401) {
                        logToServer('error', 'Token refresh failed with 401 during GraphQL request. Logging out user.', {error: refreshError});
                        api.dispatch(logOut());
                        // After a failed refresh, return the original error to avoid a pointless retry.
                        return result;
                    }
                    else {
                        logToServer('error', 'Token refresh failed with a non-401 error during GraphQL request.', {error: refreshError});
                    }
                }
                finally {
                    release();
                    logToServer('info', 'graphqlBaseQueryWithReauth: Releasing mutex.');
                }
            }
        }
        // Retry the request after the mutex has been released.
        result = await graphqlBaseQuery(args, api, extraOptions);
    }

    return result;
}

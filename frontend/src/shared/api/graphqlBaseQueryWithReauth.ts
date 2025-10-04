import {ClientError, GraphQLClient} from "graphql-request";
import {BaseQueryFn} from "@reduxjs/toolkit/query/react";
import {logToServer, toString} from "../utils/loggingService";
import {logOut} from "../../features/authentication/authSlice";
import {userApiSlice} from "./userApiSlice";

interface GraphQlQueryError {
    status: number;
    data: any;
}

const client = new GraphQLClient(`${window.location.origin}/graphql`, {
    credentials: 'include',
});

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
    let result = await graphqlBaseQuery(args, api, extraOptions);

    if (result.error) {
        const hasAuthError = Array.isArray(result.error?.data) && result.error.data.some(
            (e: any) => e.extensions?.classification === 'UNAUTHORIZED' ||
                         e.extensions?.classification === 'FORBIDDEN' ||
                         e.message.includes('Access Denied')
        );
        if (hasAuthError) {
            logToServer('debug', 'Received GraphQL auth error. Attempting to refresh token.');

            try {
                await api.dispatch(userApiSlice.endpoints.refreshToken.initiate()).unwrap();
                logToServer('debug', 'Token refresh successful. Retrying original GraphQL request.');
                result = await graphqlBaseQuery(args, api, extraOptions);
            }
            catch (refreshError: any) {
                // The error object from `unwrap()` contains status and data properties.
                // If the refresh token fails with 401, it means the session is truly expired.
                if (refreshError.status === 401) {
                    logToServer('error', 'Token refresh failed with 401 during GraphQL request. Logging out user.', {error: refreshError});
                    api.dispatch(logOut());
                }
                else {
                    logToServer('error', 'Token refresh failed with a non-401 error during GraphQL request.', {error: refreshError});
                }
            }
        }
    }

    return result;
}

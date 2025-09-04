import { createApi, BaseQueryFn } from "@reduxjs/toolkit/query/react";
import { ClientError, GraphQLClient } from "graphql-request";
import { logOut } from "../state/authSlice";
import {logToServer} from "../../utils/loggingService";

/*const client = new GraphQLClient('http://localhost:8080/graphql', {
    credentials: 'include',
});*/

const client = new GraphQLClient('/graphql');

const graphqlBaseQuery: BaseQueryFn<
        { body: string; variables?: any },
        unknown,
        unknown
> = async ({ body, variables }, { dispatch }) => {
        try {
            const result = await client.request(body, variables);
            return { data: result };
        }
        catch (error) {
            if (error instanceof ClientError) {
                const isAuthError = error.response.errors?.some(
                    (e: any) => e.extensions?.classification === 'UNAUTHORIZED'
                );

                if (isAuthError) {
                    console.error('GraphQL Authentication Error, logging out client.');
                    logToServer('error', 'GraphQL Authentication Error, logging out client.', { error });
                    dispatch(logOut());
                    window.location.replace('/login');
                }

                return { error: { status: error.response.status, data: error.response.errors } };
            }
            if (error instanceof Error) {
                logToServer('error', error.message, { error });
                return { error: { status: 'FETCH_ERROR', data: error.message } };
            }
            return { error: { status: 500, data: 'An unknown error occurred' } };
        }
    };

export const graphqlApiSlice = createApi({
    reducerPath: 'graphqlApi',
    baseQuery: graphqlBaseQuery,
    tagTypes: ['Book', 'Chapter'],
    endpoints: builder => ({}),
});
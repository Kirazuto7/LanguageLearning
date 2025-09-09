import { createApi, BaseQueryFn } from "@reduxjs/toolkit/query/react";
import { ClientError, GraphQLClient } from "graphql-request";
import { userApiSlice } from "./userApiSlice";
import { logToServer } from "../utils/loggingService";
import { toString } from "../utils/loggingService";


const client = new GraphQLClient(`${window.location.origin}/graphql`, {
    credentials: 'include',
});

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
                    dispatch(userApiSlice.endpoints.logout.initiate());
                    window.location.replace('/login');
                }

                logToServer('error', "Client error occurred.", toString(error.response.errors));
                return { error: { status: error.response.status, data: error.response.errors } };
            }
            return { error: { status: 500, data: 'An unknown server error occurred' } };
        }
    };

export const graphqlApiSlice = createApi({
    reducerPath: 'graphqlApi',
    baseQuery: graphqlBaseQuery,
    tagTypes: ['Book', 'Chapter'],
    endpoints: builder => ({}),
});
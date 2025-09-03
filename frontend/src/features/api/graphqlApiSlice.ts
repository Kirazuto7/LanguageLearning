import { createApi, BaseQueryFn } from "@reduxjs/toolkit/query/react";
import { request, ClientError } from "graphql-request";

const graphqlBaseQuery =
    ({ baseUrl }: { baseUrl: string }): BaseQueryFn<
        { body: string; variables?: any },
        unknown,
        unknown
    > =>
    async ({ body, variables }) => {
        try {
            const result = await request(baseUrl, body, variables);
            return { data: result };
        }
        catch (error) {
            if (error instanceof ClientError) {
                return { error: { status: error.response.status, data: error } };
            }
            return { error: { status: 500, data: error } };
        }
    };

export const graphqlApiSlice = createApi({
    reducerPath: 'graphqlApi',
    baseQuery: graphqlBaseQuery({
        baseUrl: 'http://localhost:8080/graphql',
    }),
    tagTypes: ['Book', 'Chapter'],
    endpoints: builder => ({}),
});
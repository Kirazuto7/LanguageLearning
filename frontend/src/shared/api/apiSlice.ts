import { createApi, fetchBaseQuery, BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query/react';
import { logToServer } from "../utils/loggingService";
import { userApiSlice } from "./userApiSlice";


const baseQuery = fetchBaseQuery({
    baseUrl: "/api",
})

const baseQueryWrapper: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (args, api, extraOptions) => {
    let result = await baseQuery(args, api, extraOptions);

    // Check if the query resulted in an error / Handle network errors that likely mean the server is down or restarting
    if (result.error) {
        // Handle specific HTTP status codes for authentication
        if (result.error.status === 401 || result.error.status === 403) {
            console.error('Session invalid, logging out client.');
            logToServer('error', 'Session invalid, logging out client.', { error: result.error });
            api.dispatch(userApiSlice.endpoints.logout.initiate());
            window.location.replace('/login');
        }
    }
    return result;
}


// Service w/ base url and endpoints
export const apiSlice = createApi({
    reducerPath: 'api', // Name of the slicer in the store
    baseQuery: baseQueryWrapper,
    tagTypes: ['User', 'Book'], // Labels for caching and invalidation
    endpoints: builder => ({}) // API Endpoints will be injected into this builder
});
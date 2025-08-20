import { createApi, fetchBaseQuery, BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query/react';
import { logOut } from '../state/authSlice';


const baseQuery = fetchBaseQuery({
    baseUrl: "http://localhost:8080/api",
    credentials: "include",
});

const baseQueryWrapper: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (args, api, extraOptions) => {
    let result = await baseQuery(args, api, extraOptions);

    // Check if the query resulted in an error / Handle network errors that likely mean the server is down or restarting
    if (result.error) {
        // Handle specific HTTP status codes for authentication
        if (result.error.status === 401 || result.error.status === 403 || result.error.status === 'FETCH_ERROR') {
            console.error('Session invalid, logging out client.');
            api.dispatch(logOut());
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
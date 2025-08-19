import { createApi, fetchBaseQuery, BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query/react';
import { logOut } from '../state/authSlice';


const baseQuery = fetchBaseQuery({
    baseUrl: "http://localhost:8080/api",
    credentials: "include",
});

const baseQueryWrapper: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (args, api, extraOptions) => {
    let result = await baseQuery(args, api, extraOptions);

    if(result.error && (result.error.status === 401 || result.error.status === 403)) {
        console.error('Authentication error detected, logging out client.');
        api.dispatch(logOut());
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
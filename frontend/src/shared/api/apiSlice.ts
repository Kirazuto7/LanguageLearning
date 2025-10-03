import { createApi } from '@reduxjs/toolkit/query/react';
import {baseQueryWithReauth} from "./baseQueryWithReauth";



// Service w/ base url and endpoints
export const apiSlice = createApi({
    reducerPath: 'api', // Name of the slicer in the store
    baseQuery: baseQueryWithReauth,
    tagTypes: ['User', 'Book'], // Labels for caching and invalidation
    endpoints: builder => ({}) // API Endpoints will be injected into this builder
});
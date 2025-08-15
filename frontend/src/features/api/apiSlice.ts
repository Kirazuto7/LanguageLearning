import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

// Service w/ base url and endpoints
export const apiSlice = createApi({
    reducerPath: 'api', // Name of the slicer in the store
    baseQuery: fetchBaseQuery({baseUrl: "/api"}), // Prefix url for all api requests
    tagTypes: ['User', 'Book'], // Labels for caching and invalidation
    endpoints: builder => ({}) // API Endpoints will be injected into this builder
});
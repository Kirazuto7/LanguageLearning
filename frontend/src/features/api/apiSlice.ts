import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';


const getCookie = (name: string) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop()?.split(';').shift();
}

// Service w/ base url and endpoints
export const apiSlice = createApi({
    reducerPath: 'api', // Name of the slicer in the store
    baseQuery: fetchBaseQuery({
        baseUrl: "http://localhost:8080/api",
        credentials: "include",
        prepareHeaders: (headers) => {
            const token = getCookie('XSRF-TOKEN');
            if (token) {
                headers.set('X-XSRF-TOKEN', token);
            }
            return headers;
        },
    }),
    tagTypes: ['User', 'Book'], // Labels for caching and invalidation
    endpoints: builder => ({}) // API Endpoints will be injected into this builder
});
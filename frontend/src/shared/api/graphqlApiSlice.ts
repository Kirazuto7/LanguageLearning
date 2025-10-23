import { createApi } from "@reduxjs/toolkit/query/react";
import {graphqlBaseQueryWithReauth} from "./graphqlBaseQueryWithReauth";

export const graphqlApiSlice = createApi({
    reducerPath: 'graphqlApi',
    baseQuery: graphqlBaseQueryWithReauth,
    tagTypes: ['Book', 'Chapter'],
    endpoints: builder => ({}),
});
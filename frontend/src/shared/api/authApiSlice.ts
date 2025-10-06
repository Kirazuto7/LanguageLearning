import { createApi } from "@reduxjs/toolkit/query/react";
import {UserDTO} from "../types/dto";
import {baseQuery} from "./baseQuery";
import {HttpMethod} from "../types/types";

export const authApiSlice =  createApi({
    reducerPath: 'authApi',
    baseQuery: baseQuery,
    endpoints: (builder) => ({
        refreshToken: builder.mutation<UserDTO, void>({
            query: () => ({
                url: '/users/refresh',
                method: HttpMethod.POST,
            }),
        })
    })
});

export const { useRefreshTokenMutation } = authApiSlice;

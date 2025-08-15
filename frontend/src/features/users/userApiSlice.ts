import { apiSlice } from "../api/apiSlice";
import { LoginRequest, CreateUserRequest, UserDTO } from "../../types/dto";

export const userApiSlice = apiSlice.injectEndpoints({
    endpoints: builder => ({
        login: builder.mutation<UserDTO, LoginRequest>({
            query: credentials => ({
                url:'/users/login',
                method: 'POST',
                body: credentials
            })
        }),

        register: builder.mutation<UserDTO, CreateUserRequest>({
            query: userData => ({
                url: '/users/register',
                method: 'POST',
                body: userData
            })
        }),
    })
});

export const { useLoginMutation, useRegisterMutation } = userApiSlice;
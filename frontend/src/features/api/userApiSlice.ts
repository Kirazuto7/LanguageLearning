import { apiSlice } from "../api/apiSlice";
import { LoginRequest, CreateUserRequest, UserDTO, UpdateSettingsRequest, SettingsDTO } from "../../types/dto";

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

        updateSettings: builder.mutation<SettingsDTO, UpdateSettingsRequest>({
            query: ({ userId, settings }) => ({
                url: `/users/${userId}/settings`,
                method: 'PATCH',
                body: settings,
            })
        }),
    })
});

export const { useLoginMutation, useRegisterMutation, useUpdateSettingsMutation } = userApiSlice;
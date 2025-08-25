import { apiSlice } from "../api/apiSlice";
import { LoginRequest, CreateUserRequest, SettingsDTO, UserDTO } from "../../types/dto";

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

        logout: builder.mutation<void, void>({
            query: () => ({
                url: '/users/logout',
                method: 'POST',
            })
        }),

        updateSettings: builder.mutation<SettingsDTO, Partial<SettingsDTO>>({
            query: (settingsData) => ({
                url: `/users/settings`,
                method: 'PATCH',
                body: settingsData,
            })
        }),

        healthCheck: builder.query<void, void>({
            query: () => ({
                url: '/users/health',
                method: 'GET',
            })
        })
    })
});

export const { useLoginMutation, useRegisterMutation, useLogoutMutation, useUpdateSettingsMutation, useHealthCheckQuery } = userApiSlice;
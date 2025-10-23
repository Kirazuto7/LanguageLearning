import { createApi } from "@reduxjs/toolkit/query/react";
import {
    CompleteOidcRegistrationRequest,
    CreateUserRequest,
    LoginRequest,
    UserDTO
} from "../types/dto";
import {baseQuery} from "./baseQuery";
import {HttpMethod} from "../types/types";
import {closeWsClient} from "../../app/services/wsClient";
import {closeSseClient} from "../../app/services/sseClient";
import {logOut} from "../../features/authentication/authSlice";
import {logToServer} from "../utils/loggingService";

export const authApiSlice =  createApi({
    reducerPath: 'authApi',
    baseQuery: baseQuery,
    endpoints: (builder) => ({
        login: builder.mutation<UserDTO, LoginRequest>({
            query: credentials => ({
                url:'/users/login',
                method: HttpMethod.POST,
                body: credentials
            })
        }),

        register: builder.mutation<UserDTO, CreateUserRequest>({
            query: userData => ({
                url: '/users/register',
                method: HttpMethod.POST,
                body: userData
            })
        }),

        completeOidcRegistration: builder.mutation<UserDTO, CompleteOidcRegistrationRequest>({
            query: (request) => ({
                url: '/users/complete-oidc-registration',
                method: HttpMethod.POST,
                body: request
            })
        }),

        logout: builder.mutation<void, void>({
            query: () => ({
                url: '/users/logout',
                method: HttpMethod.POST,
            }),
            async onQueryStarted(args, { dispatch, queryFulfilled }) {
                try {
                    await queryFulfilled;
                    closeWsClient();
                    closeSseClient();
                    localStorage.removeItem('user');
                    localStorage.removeItem('persist:root');
                    dispatch(logOut());
                } catch (err) {
                    logToServer('error', 'Logout API call failed, forcing local logout.', err);
                    closeWsClient();
                    closeSseClient();
                    localStorage.removeItem('user');
                    localStorage.removeItem('persist:root');
                    dispatch(logOut());
                }
            }
        }),

        refreshToken: builder.mutation<UserDTO, void>({
            query: () => ({
                url: '/users/refresh',
                method: HttpMethod.POST,
            }),
        }),

        healthCheck: builder.query<void, void>({
            query: () => ({
                url: '/users/health',
                method: HttpMethod.GET,
            })
        })
    })
});

export const {
    useLoginMutation,
    useRegisterMutation,
    useCompleteOidcRegistrationMutation,
    useLogoutMutation,
    useRefreshTokenMutation,
    useHealthCheckQuery
} = authApiSlice;

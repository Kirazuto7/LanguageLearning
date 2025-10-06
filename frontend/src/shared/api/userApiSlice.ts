import { apiSlice } from "./apiSlice";
import {LoginRequest, CreateUserRequest, SettingsDTO, UserDTO, CompleteOidcRegistrationRequest} from "../types/dto";
import {logOut} from "../../features/authentication/authSlice";
import {logToServer} from "../utils/loggingService";

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

        completeOidcRegistration: builder.mutation<UserDTO, CompleteOidcRegistrationRequest>({
            query: (request) => ({
                url: '/users/complete-oidc-registration',
                method: 'POST',
                body: request
            })
        }),

        logout: builder.mutation<void, void>({
            query: () => ({
                url: '/users/logout',
                method: 'POST',
            }),
            async onQueryStarted(args, { dispatch, queryFulfilled }) {
                try {
                    // Wait for the API call to complete successfully.
                    await queryFulfilled;
                    // --- THIS IS THE CORE LOGIC ---
                    // 1. Clear the persisted storage directly.
                    localStorage.clear();
                    // 2. Dispatch the action to clear the in-memory state.
                    dispatch(logOut());
                    // --- END OF CORE LOGIC ---
                } catch (err) {
                    // If the API call fails, we should still force a local logout
                    // to ensure the user is not left in a broken state.
                    logToServer('error', 'Logout API call failed, forcing local logout.', err);
                    localStorage.clear();
                    dispatch(logOut());
                }
            }
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

export const {
    useLoginMutation,
    useRegisterMutation,
    useCompleteOidcRegistrationMutation,
    useLogoutMutation,
    useUpdateSettingsMutation,
    useHealthCheckQuery
} = userApiSlice;
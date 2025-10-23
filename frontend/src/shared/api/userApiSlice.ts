import { apiSlice } from "./apiSlice";
import {
    SettingsDTO,
    UserDataDTO
} from "../types/dto";
import {HttpMethod} from "../types/types";

export const userApiSlice = apiSlice.injectEndpoints({
    endpoints: builder => ({
        updateSettings: builder.mutation<SettingsDTO, Partial<SettingsDTO>>({
            query: (settingsData) => ({
                url: '/users/settings',
                method: HttpMethod.PATCH,
                body: settingsData,
            })
        }),

        getUserDashboardData: builder.query<UserDataDTO, void>({
            query: () => ({
                url: '/users/dashboard',
                method: HttpMethod.GET
            }),
            providesTags: [{ type: 'Book', id: 'LIST' }],
        })
    })
});

export const {
    useUpdateSettingsMutation,
    useGetUserDashboardDataQuery,
} = userApiSlice;
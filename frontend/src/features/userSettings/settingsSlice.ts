import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { SettingsDTO, UserDTO } from "../../shared/types/dto";
import { userApiSlice } from "../../shared/api/userApiSlice";
import { logOut } from "../authentication/authSlice";
import {RootState} from "../../app/store";
import {authApiSlice} from "../../shared/api/authApiSlice";

interface SettingsState {
    settings: SettingsDTO | null;
}

const initialState: SettingsState = {
    settings: null,
};

export const settingsSlice = createSlice({
    name: 'settings',
    initialState,
    reducers: {
        syncSettings: (state, action: PayloadAction<SettingsDTO>) => {
            state.settings = action.payload;
        }
    },
    extraReducers: (builder) => {
         builder.addCase(logOut, (state) => {
            state.settings = null;
        });

        builder.addMatcher(
            (action): action is PayloadAction<UserDTO> =>
                authApiSlice.endpoints.login.matchFulfilled(action) ||
                authApiSlice.endpoints.register.matchFulfilled(action) ||
                authApiSlice.endpoints.refreshToken.matchFulfilled(action) ||
                authApiSlice.endpoints.completeOidcRegistration.matchFulfilled(action),
            (state, { payload }) => {
                state.settings = payload.settings;
            }
        );

        builder.addMatcher(
           (action): action is PayloadAction<SettingsDTO> =>
                userApiSlice.endpoints.updateSettings.matchFulfilled(action),
           (state, { payload }: PayloadAction<SettingsDTO>) => {
               state.settings = payload;
          }
        );
    }
})

export const selectCurrentSettings = (state: RootState) => state.settings.settings;
export const { syncSettings } = settingsSlice.actions;
export const selectCurrentTheme = (state: RootState) => state.settings.settings?.theme ?? 'default';
export default settingsSlice.reducer;
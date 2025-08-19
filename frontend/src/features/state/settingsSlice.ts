import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { SettingsDTO, UserDTO } from "../../types/dto";
import { userApiSlice } from "../api/userApiSlice";
import { logOut } from "./authSlice";

interface SettingsState {
    settings: SettingsDTO | null;
}

const storedSettings = localStorage.getItem('settings');

const initialState: SettingsState = {
    settings: storedSettings ? JSON.parse(storedSettings) : null,
};

export const settingsSlice = createSlice({
    name: 'settings',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
         builder.addCase(logOut, (state) => {
            state.settings = null;
            localStorage.removeItem('settings');
        });

        builder.addMatcher(
            (action): action is PayloadAction<UserDTO> =>
                userApiSlice.endpoints.login.matchFulfilled(action) ||
                userApiSlice.endpoints.register.matchFulfilled(action),
            (state, { payload }) => {
                state.settings = payload.settings;
                localStorage.setItem('settings', JSON.stringify(payload.settings));
            }
        );

        builder.addMatcher(
           (action): action is PayloadAction<SettingsDTO> =>
                userApiSlice.endpoints.updateSettings.matchFulfilled(action),
           (state, { payload }: PayloadAction<SettingsDTO>) => {
               state.settings = payload;
               localStorage.setItem('settings', JSON.stringify(payload));
          }
        );
    }
})

export const {} = settingsSlice.actions;
export default settingsSlice.reducer;
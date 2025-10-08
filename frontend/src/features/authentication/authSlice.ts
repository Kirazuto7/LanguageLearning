import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { UserDTO } from "../../shared/types/dto";
import { userApiSlice } from "../../shared/api/userApiSlice";
import { RootState } from "../../app/store";
import { logToServer } from "../../shared/utils/loggingService";
import { authApiSlice } from "../../shared/api/authApiSlice";

/*//////////////////////////////////////////////////////////////////////////////////////*/
/*     This Redux "slice" will manage the user's authentication state on                */
/*     the client side (i.e., storing the user object after a successful login).        */
/*//////////////////////////////////////////////////////////////////////////////////////*/

interface AuthState {
    user: UserDTO | null;
}

const loadUserFromStorage = (): UserDTO | null => {
    try {
        const storedUser = localStorage.getItem('user');
        if(storedUser === null) {
            return null;
        }
        return JSON.parse(storedUser);
    }
    catch (error) {
        logToServer("error", "Failed to parse user from localStorage", error);
        localStorage.removeItem('user');
        return null;
    }
};

const initialState: AuthState = {
    user: loadUserFromStorage(),
};

const handleUserLogout = (state: AuthState) => {
    state.user = null;
    localStorage.clear();
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        logOut: handleUserLogout,
    },
    extraReducers: (builder) => {
        /*/////////////////////////////////////////////////////*/
        /*  When the mutators execute (e.g.: login, register)  */
        /*/////////////////////////////////////////////////////*/

        builder.addMatcher(
            (action): action is PayloadAction<UserDTO> =>
                authApiSlice.endpoints.login.matchFulfilled(action) || // Login
                authApiSlice.endpoints.register.matchFulfilled(action) || // Register
                authApiSlice.endpoints.refreshToken.matchFulfilled(action) || // Refresh Token
                authApiSlice.endpoints.completeOidcRegistration.matchFulfilled(action), // Complete OIDC Registration
            (state, { payload }) => {
                state.user = payload;
                localStorage.setItem('user', JSON.stringify(payload));
            }
        );
        
        // User initiated via api
        builder.addMatcher(
            authApiSlice.endpoints.logout.matchFulfilled,
            handleUserLogout
        );

        builder.addMatcher(
            userApiSlice.endpoints.updateSettings.matchFulfilled,
            (state, { payload }) => {
                if(state.user) {
                    state.user.settings = payload;
                    localStorage.setItem('user', JSON.stringify(state.user));
                }
            }
        );
    }
});

export const { logOut } = authSlice.actions;
export default authSlice.reducer;

/*/////////////////////////////////////////////////////*/
/*                    Selectors                        */
/*/////////////////////////////////////////////////////*/
export const selectCurrentUser = (state: RootState) => state.auth.user;
export const selectIsAuthenticated = (state: RootState) => !!state.auth.user;
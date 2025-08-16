import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { UserDTO } from "../../types/dto";
import { userApiSlice } from "../api/userApiSlice";

/*//////////////////////////////////////////////////////////////////////////////////////*/
/*     This Redux "slice" will manage the user's authentication state on                */
/*     the client side (i.e., storing the user object after a successful login).        */
/*//////////////////////////////////////////////////////////////////////////////////////*/

interface AuthState {
    user: UserDTO | null;
}

const storedUser = localStorage.getItem('user');

const initialState: AuthState = {
    user: storedUser ? JSON.parse(storedUser) : null,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        logOut: (state) => {
            state.user = null;
            localStorage.removeItem('user');
        },
    },
    extraReducers: (builder) => {
        /*/////////////////////////////////////////////////////*/
        /*  When the mutators execute (e.g.: login, register)  */
        /*/////////////////////////////////////////////////////*/

        builder.addMatcher(
            (action): action is PayloadAction<UserDTO> =>
                userApiSlice.endpoints.login.matchFulfilled(action) || // Login
                userApiSlice.endpoints.register.matchFulfilled(action), // Register
            (state, { payload }) => {
                state.user = payload;
                localStorage.setItem('user', JSON.stringify(payload));
            }
        )
    }
});

export const { logOut } = authSlice.actions;
export default authSlice.reducer;
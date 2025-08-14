import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { UserDTO } from "../../types/dto";
import { userApiSlice } from "../users/userApiSlice";

/*//////////////////////////////////////////////////////////////////////////////////////*/
/* This is a standard Redux "slice" that will manage the user's authentication state on */
/*     the client side (i.e., storing the user object after a successful login).        */
/*//////////////////////////////////////////////////////////////////////////////////////*/

interface AuthState {
    user: UserDTO | null;
}

const initialState: AuthState = {
    user: null,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        // On App load
        setUser: (state, action: PayloadAction<UserDTO>) => {
            state.user = action.payload;
        },
    },
    extraReducers: (builder) => {
        /*/////////////////////////////////////////////////////*/
        /*  When the mutators execute (e.g.: login, register)  */
        /*/////////////////////////////////////////////////////*/

        // Login
        builder.addMatcher(userApiSlice.endpoints.login.matchFulfilled, (state, { payload }) =>{
            state.user = payload;
        });

        // Register
        builder.addMatcher(userApiSlice.endpoints.register.matchFulfilled, (state, { payload }) => {
            state.user = payload;
        })
    }
});

export const { setUser } = authSlice.actions;
export default authSlice.reducer;
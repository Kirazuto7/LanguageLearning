import { configureStore } from "@reduxjs/toolkit";
import { apiSlice } from "../features/api/apiSlice";
import authReducer from "../features/auth/authSlice";

export const store = configureStore({
    reducer: {
        [apiSlice.reducerPath]: apiSlice.reducer, // top-level slice reducer
        auth: authReducer,
    },
    /*//////////////////////////////////////////////////////////////*/
    /* Adding the api middleware enables rtk-query features such as */
    /*         caching, invalidation, polling, etc...               */
    /*//////////////////////////////////////////////////////////////*/
    middleware: getDefaultMiddleware =>
        getDefaultMiddleware().concat(apiSlice.middleware)
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
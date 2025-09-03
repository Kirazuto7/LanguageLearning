import { configureStore } from "@reduxjs/toolkit";
import { apiSlice } from "../features/api/apiSlice";
import authReducer from "../features/state/authSlice";
import settingsReducer from "../features/state/settingsSlice";
import {graphqlApiSlice} from "../features/api/graphqlApiSlice";

export const store = configureStore({
    reducer: {
        [apiSlice.reducerPath]: apiSlice.reducer, // top-level slice reducer
        [graphqlApiSlice.reducerPath]: graphqlApiSlice.reducer,
        auth: authReducer,
        settings: settingsReducer,
    },
    /*//////////////////////////////////////////////////////////////*/
    /* Adding the api middleware enables rtk-query features such as */
    /*         caching, invalidation, polling, etc...               */
    /*//////////////////////////////////////////////////////////////*/
    middleware: getDefaultMiddleware =>
        getDefaultMiddleware()
            .concat(apiSlice.middleware)
            .concat(graphqlApiSlice.middleware)
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
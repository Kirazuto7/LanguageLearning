import { configureStore } from "@reduxjs/toolkit";
import { apiSlice } from "../shared/api/apiSlice";
import authReducer from "../features/authentication/authSlice";
import settingsReducer from "../features/userSettings/settingsSlice";
import {graphqlApiSlice} from "../shared/api/graphqlApiSlice";
import progressReducer from "../widgets/progressBar/progressSlice";

export const store = configureStore({
    reducer: {
        [apiSlice.reducerPath]: apiSlice.reducer, // top-level slice reducer
        [graphqlApiSlice.reducerPath]: graphqlApiSlice.reducer,
        auth: authReducer,
        settings: settingsReducer,
        progress: progressReducer
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
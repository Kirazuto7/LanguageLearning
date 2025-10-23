import {combineReducers, configureStore} from "@reduxjs/toolkit";
import {apiSlice} from "../shared/api/apiSlice";
import authReducer from "../features/authentication/authSlice";
import settingsReducer from "../features/userSettings/settingsSlice";
import {graphqlApiSlice} from "../shared/api/graphqlApiSlice";
import progressReducer from "../widgets/progressBar/progressSlice";
import { broadcastMiddleware } from "./broadcastMiddleware";
import {FLUSH, persistReducer, persistStore, REHYDRATE} from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import {PAUSE, PERSIST, PURGE, REGISTER} from "redux-persist/es/constants";
import {authApiSlice} from "../shared/api/authApiSlice";
import {setupListeners} from "@reduxjs/toolkit/query";
import {initializePolling} from "./services/pollingManager";

const rootReducer = combineReducers({
    [apiSlice.reducerPath]: apiSlice.reducer,
    [authApiSlice.reducerPath]: authApiSlice.reducer,
    [graphqlApiSlice.reducerPath]: graphqlApiSlice.reducer,
    auth: authReducer,
    settings: settingsReducer,
    progress: progressReducer,
});

const persistConfig = {
    key: 'root',
    storage,
    whitelist: ['auth', 'progress', 'settings'] // Persisted states
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
    reducer: persistedReducer,
    /*//////////////////////////////////////////////////////////////*/
    /* Adding the api middleware enables rtk-query features such as */
    /*         caching, invalidation, polling, etc...               */
    /*//////////////////////////////////////////////////////////////*/
    middleware: getDefaultMiddleware =>
        getDefaultMiddleware({
            serializableCheck: {
                ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
            },
        })
            .concat(apiSlice.middleware)
            .concat(authApiSlice.middleware)
            .concat(graphqlApiSlice.middleware)
            .concat(broadcastMiddleware)
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export const persistor = persistStore(store);
setupListeners(store.dispatch);

persistor.subscribe(() => {
    const { bootstrapped } = persistor.getState();
    if (bootstrapped) {
        initializePolling();
    }
});
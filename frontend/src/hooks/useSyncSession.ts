import { useEffect } from "react";
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "../features/authentication/authSlice";
import { useRefreshTokenMutation } from "../shared/api/userApiSlice";
import {logToServer} from "../shared/utils/loggingService";
/**
 * This hook synchronizes the application's session state after an external OIDC redirect.
 * It checks if the user is authenticated in Redux. If not, it attempts to use the
 * refresh token cookie to re-establish the session.
 */
export const useSyncSession = () => {
    const isAuthenticated = useSelector(selectIsAuthenticated);
    const [refreshToken, { isLoading }] = useRefreshTokenMutation();

    useEffect(() => {
        logToServer('debug', 'useSyncSession effect running.');
        // If the user is not authenticated in Redux state, but a refresh might be possible
        if (!isAuthenticated) {
            logToServer('info', 'User is not authenticated in Redux, attempting session sync.');
            refreshToken().unwrap().catch(() => {
                logToServer('debug', 'Session sync failed, user is not logged in.');
            })
        }
    }, []);

    return isLoading;
};
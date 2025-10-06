import {useSelector} from "react-redux";
import {selectIsAuthenticated} from "../features/authentication/authSlice";
import {useRefreshTokenMutation} from "../shared/api/authApiSlice";
import {useEffect, useState} from "react";
import FullScreenLoader from "../shared/components/fullscreenLoader/FullScreenLoader";


/**
 * This component synchronizes the application's session state on initial load.
 * It's intended to be rendered once at the top level of the application.
 * It checks if the user is authenticated in Redux. If not, it attempts to use the
 * refresh token cookie to re-establish the session (e.g., after an OIDC redirect).
 */
const SessionSynchronizer: React.FC = () => {
    const isAuthenticated = useSelector(selectIsAuthenticated);
    const [refreshToken, { isLoading }] = useRefreshTokenMutation();
    const [isSyncing, setIsSyncing] = useState(!isAuthenticated);

    useEffect(() => {
        if (!isAuthenticated) {
            refreshToken().unwrap()
                .catch(() => {})
                .finally(() => setIsSyncing(false));
        }
    }, []);
    // isAuthenticated, refreshToken?
    return isSyncing ? <FullScreenLoader/> : null;
};
export default  SessionSynchronizer;
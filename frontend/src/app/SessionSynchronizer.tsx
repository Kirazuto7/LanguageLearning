import {useSelector} from "react-redux";
import {selectIsAuthenticated} from "../features/authentication/authSlice";
import {useRefreshTokenMutation} from "../shared/api/authApiSlice";
import {useEffect, useRef} from "react";
import {useLocation} from "react-router-dom";
import {publicPaths} from "../shared/types/options";

interface SessionSynchronizerProps {
    onSyncComplete: () => void;
}

/**
 * This component synchronizes the application's session state on initial load.
 * It's intended to be rendered once at the top level of the application.
 * It checks if the user is authenticated in Redux. If not, it attempts to use the
 * refresh token cookie to re-establish the session.
 */
const SessionSynchronizer: React.FC<SessionSynchronizerProps> = ({ onSyncComplete }) => {
    const isAuthenticated = useSelector(selectIsAuthenticated);
    const location = useLocation();
    const hasSynced = useRef(false);
    const [refreshToken] = useRefreshTokenMutation();

    useEffect(() => {
        // This effect should only ever run once on initial application mount.
        if (hasSynced.current) {
            return;
        }
        hasSynced.current = true;

        const isPublicPath = publicPaths.includes(location.pathname);

        // If we are already authenticated or on a public path, no sync is needed.
        if (isAuthenticated || isPublicPath) {
            onSyncComplete();
            return;
        }

        // Attempt to refresh the session and then signal completion.
        refreshToken().unwrap()
            .catch(() => { /* We don't need to handle the error, the user will remain logged out */ })
            .finally(onSyncComplete);

    // The dependency array is intentionally minimal to ensure this runs only once.
    }, [isAuthenticated, onSyncComplete, refreshToken]);

    return null;
};

export default SessionSynchronizer;
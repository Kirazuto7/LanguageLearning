import { useSelector } from "react-redux";
import { RootState } from "../../app/store";
import { useHealthCheckQuery } from "../../features/api/userApiSlice";

const SessionManager = () => {
    const { user } = useSelector((state: RootState) => state.auth);

    const POLLING_INTERVAL = 30000;

    useHealthCheckQuery(undefined, {
        pollingInterval: POLLING_INTERVAL,
        skip: !user, // Ensure this only runs when a user is logged in
    });

    return null; // No DOM rendering
};

export default SessionManager;
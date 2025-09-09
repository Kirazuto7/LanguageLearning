import React from "react";
import {useSelector} from "react-redux";
import {RootState} from "../../../app/store";
import {Navigate, Outlet} from "react-router-dom";

const ProtectedRoute: React.FC = () => {
    const { user } = useSelector((state: RootState) => state.auth);

    if(!user) {
        // Redirect unauthenticated users back to the landing page.
        return <Navigate to="/" replace/>
    }

    return <Outlet />;
}

export default ProtectedRoute;
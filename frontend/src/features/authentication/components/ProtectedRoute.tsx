import React from "react";
import { useSelector } from "react-redux";
import { Navigate, Outlet } from "react-router-dom";
import { selectIsAuthenticated } from "../authSlice";

const ProtectedRoute: React.FC = () => {
    const isAuthenticated = useSelector(selectIsAuthenticated);

    if(!isAuthenticated) {
        // Redirect unauthenticated users back to the landing page.
        return <Navigate to="/" replace/>
    }

    return <Outlet />;
}

export default ProtectedRoute;
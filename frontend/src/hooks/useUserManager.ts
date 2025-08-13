import { useState, useCallback } from "react";
import { CreateUserRequest, LoginRequest, UserDTO } from "../types/dto";
/* ----------------------------------------------------------- */
/* ---         Hook to Handle Login & Registration         --- */
/* ----------------------------------------------------------- */

interface UserManagerResult {
    register: (createUserRequest: CreateUserRequest) => Promise<UserDTO | null>;
    login: (loginRequest: LoginRequest) => Promise<UserDTO | null>;
    isLoading: boolean;
    error: string | null;
    user: UserDTO | null;
}

export function useUserManager(): UserManagerResult {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [user, setUser] = useState<UserDTO | null>(null);

    const makeRequest = useCallback(async (endpoint: string, body: CreateUserRequest | LoginRequest): Promise<UserDTO | null> => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body),
            });

            if (!response.ok) {
                // Attempt to parse a more specific error message from the backend response
                const errorData = await response.json().catch(() => null);
                throw new Error(errorData?.message || `Request failed with status ${response.status}`);
            }

            const data: UserDTO = await response.json();
            setUser(data);
            return data; // Return user data on success
        } catch (err) {
            const message = err instanceof Error ? err.message : 'An unknown error occurred.';
            setError(message);
            return null; // Indicate failure
        } finally {
            setIsLoading(false);
        }
    }, []); // This function doesn't depend on changing state/props, so the dependency array is empty.

    const register = useCallback(
        (createUserRequest: CreateUserRequest) => makeRequest('/api/users/register', createUserRequest),
        [makeRequest]
    );

    const login = useCallback(
        (loginRequest: LoginRequest) => makeRequest('/api/users/login', loginRequest),
        [makeRequest]
    );

    return { register, login, isLoading, error, user };
}
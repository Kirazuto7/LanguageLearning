import React, { createContext, useContext, useMemo } from 'react';
import { useUserManager } from '../hooks/useUserManager';
import { CreateUserRequest, UserDTO, LoginRequest } from '../types/dto';

interface UserContextType {
    register: (createUserRequest: CreateUserRequest) => Promise<UserDTO | null>;
    login: (loginRequest: LoginRequest) => Promise<UserDTO | null>;
    isLoading: boolean;
    error: string | null;
    user: UserDTO | null;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const useUser = (): UserContextType => {
    const context = useContext(UserContext);
        if(context === undefined) {
            throw new Error('useUser must be within a UserProvider');
        }
        return context;
}

interface UserProviderProps{
    children: React.ReactNode;
}

export const UserProvider: React.FC<UserProviderProps> = ({children}) => {
    const { register, login, isLoading, error, user } = useUserManager();

    const value = useMemo(() => ({
        register,
        login,
        isLoading,
        error,
        user
    }), [register, login, isLoading, error, user]);

    return(
        <UserContext.Provider value={value}>
            {children}
        </UserContext.Provider>
    )
}
'use client'

import {createContext, useContext, useState, useEffect, ReactNode} from "react";
import {useRouter} from "next/navigation";

type AuthContextType = {
    isLoggedIn: boolean;
    login: (token: string) => void;
    logout: () => void;
    loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({children}: {children: ReactNode}) {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [loading, setLoading] = useState(true);
    const router = useRouter();

    useEffect(() => {
        const token = localStorage.getItem("token");
        setIsLoggedIn(!!token);
        setLoading(false);
    }, []);

    function login(token: string) {
        localStorage.setItem("token", token);
        setIsLoggedIn(true);
    }

    function logout() {
        localStorage.removeItem("token");
        setIsLoggedIn(false);
        router.push("/login");
    }

    return (
        <AuthContext.Provider value={{ isLoggedIn, login, logout , loading}}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within AuthProvider");
    }
    return context;
}
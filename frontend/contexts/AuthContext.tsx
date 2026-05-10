'use client'

import {createContext, useContext, useState, useEffect, ReactNode} from "react";
import {useRouter} from "next/navigation";
import {apiFetch} from "@/lib/api";
import {CartDetailedView} from "@/lib/types/api";

type AuthContextType = {
    isLoggedIn: boolean;
    loading: boolean;
    cartItemCount: number;
    refreshCartCount: () => void;
    login: (token: string) => void;
    logout: () => void;

}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({children}: {children: ReactNode}) {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [loading, setLoading] = useState(true);
    const [ cartItemCount, setCartItemCount] = useState(0);
    const router = useRouter();

    async function fetchCartCount() {
        const token = localStorage.getItem("token");  // Check token directly
        if (!token) {
            setCartItemCount(0);
            return;
        }
        try {
            const token = localStorage.getItem("token");
            const cart = await apiFetch<CartDetailedView>("/api/v1/carts", { token: token! });
            const totalItems = cart.items.reduce((sum, item) => sum + item.quantity, 0);
            setCartItemCount(totalItems);
        } catch (e) {
            console.error("Failed to fetch cart", e);
            setCartItemCount(0);
        }
    }

    useEffect(() => {
        const token = localStorage.getItem("token");
        setIsLoggedIn(!!token);
        setLoading(false);

        if (token) {
            fetchCartCount();
        }
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
        <AuthContext.Provider value={{isLoggedIn, loading, cartItemCount, login, logout, refreshCartCount: fetchCartCount}}>
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
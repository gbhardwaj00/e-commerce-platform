'use client'

import {useAuth} from "@/contexts/AuthContext";

export default function NavBar() {
    const {isLoggedIn, logout, loading, cartItemCount} = useAuth();

    if (loading) {
        return (
            <nav className="bg-white shadow-md py-4">
                <div className="flex justify-between items-center max-w-7xl mx-auto px-4">
                    <div>
                        <a className="text-xl font-bold text-blue-600" href="/">GetItNow</a>
                    </div>
                    <div className="flex items-center gap-4">
                        {/* Empty space while loading */}
                    </div>
                </div>
            </nav>
        );
    }

    return (
        <nav className="bg-white shadow-md py-4">
            <div className="flex justify-between items-center max-w-7xl mx-auto px-4">
                <div>
                    <a className="text-xl font-bold text-blue-600" href="/">GetItNow</a>
                </div>
                <div className="flex items-center gap-4">
                    <a className="hover:text-blue-600 font-medium mr-4" href="/products">Products</a>

                    {isLoggedIn ? (
                        <>
                            <a className="hover:text-blue-600 relative font-medium mr-4" href="/cart">
                                Cart
                                {cartItemCount > 0 && (
                                <span className="ml-1 bg-red-600 text-white text-xs px-2 py-0.5 rounded-full">
            {cartItemCount}
        </span>
                            )}
                            </a>
                            <a className="hover:text-blue-600 font-medium" href="/orders">My Orders</a>
                            <button
                                onClick={logout}
                                className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors"
                            >
                                Logout
                            </button>
                        </>
                    ) : (
                        <>
                            <a className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors" href="/login">Login</a>
                            <a className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors" href="/register">Register</a>
                        </>
                    )}

                </div>
            </div>
        </nav>
    );
}
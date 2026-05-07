'use client'

import {useState, useEffect} from "react";
import {useRouter} from "next/navigation";
import {apiFetch} from "@/lib/api";
import {CartDetailedView} from "@/lib/types/api";

export default function CartPage() {
    const [cart, setCart] = useState<CartDetailedView | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("")
    const router = useRouter();

    useEffect(() => {
        async function fetchCartItems() {
            const token = localStorage.getItem("token");
            if (!token) {
                router.push("/login");
                return
            }
            try {
                const response = await apiFetch<CartDetailedView>("/api/v1/carts", {
                    "token": token,
                })
                setCart(response);
            } catch (e) {
                setError("Failed to load cart");
                console.error(e)
            } finally {
                setLoading(false);
            }
        }
        fetchCartItems();
    }, []);

    if (loading) return <div className="max-w-7xl mx-auto p-8"><p>Loading...</p></div>;
    if (error) return <div className="max-w-7xl mx-auto p-8"><p className="text-red-600">{error}</p></div>;

    return (
        <div className="min-h-screen bg-gray-50 text-center">
            <div className="max-w-4xl mx-auto px-4 py-12">
                <h1 className="text-4xl font-bold mb-12 text-gray-900">Shopping Cart</h1>

                {cart?.items.length === 0 ? (
                    <div className="bg-white rounded-lg shadow-sm p-16">
                        <div className="text-6xl mb-6">🛒</div>
                        <h2 className="text-2xl font-semibold text-gray-800 mb-3">
                            Your cart is empty
                        </h2>
                        <p className="text-gray-600 mb-8">
                            Looks like you haven't added anything to your cart yet
                        </p>
                        <a
                            href="/products"
                            className="inline-block px-8 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
                        >
                            Start Shopping
                        </a>
                    </div>
                ) : (
                    <div>

                    </div>
                )}
            </div>
        </div>
    );
}
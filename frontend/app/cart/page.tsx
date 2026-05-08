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
                    token: token,
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

    async function removeItem(productId: string) {
        const token = localStorage.getItem("token");
        try {
            const response = await apiFetch<CartDetailedView>(
                `/api/v1/carts/items/${productId}`, {
                    method: 'DELETE',
                    token: token!
                }
            );
            setCart(response)
        } catch (e) {
            console.log("Failed to remove item", e);
        }
    }

    async function updateQuantity(newQuantity: number, productId: string) {
        if (newQuantity < 1) return;
        const token = localStorage.getItem("token");
        try {
            const response = await apiFetch<CartDetailedView>(
                `/api/v1/carts/items/${productId}`, {
                    method: 'PUT',
                    token: token!,
                    body: {
                        newQuantity
                    }
                }
            );
            setCart(response)
        } catch (e: any) {
            console.error("Failed to update quantity", e);
            let errorMsg = "Failed to update quantity";
            try {
                const parsed = JSON.parse(e.message);
                errorMsg = parsed.message || errorMsg;
            } catch {
                errorMsg = e.message || errorMsg;
            }
            alert(errorMsg);
        }
    }

    return (
        <div className="min-h-screen bg-gray-50">
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
                    <>
                        <div className="bg-white rounded-lg shadow-sm divide-y">
                            {cart?.items.map(item => (
                                <div key={item.productId} className="p-6 grid grid-cols-[1fr_auto_120px_auto] gap-8 items-center">
                                    <div>
                                        <h3 className="font-semibold text-lg">{item.title}</h3>
                                        <p className="text-gray-600">${(item.priceCents / 100).toFixed(2)}</p>
                                    </div>
                                    <div className="flex items-center gap-4">
                                        <button
                                            onClick={() => updateQuantity(item.quantity - 1, item.productId)}
                                            className="px-3 py-1 border rounded">-</button>
                                        <span className="w-8 text-center">{item.quantity}</span>
                                        <button className="px-3 py-1 border rounded"
                                                onClick={() => updateQuantity(item.quantity + 1, item.productId)}>+</button>
                                    </div>
                                    <div className="text-right">
                                        <p className="font-semibold">${(item.lineTotalCents / 100).toFixed(2)}</p>
                                    </div>
                                    <button onClick={() => removeItem(item.productId)}
                                        className="text-red-600 hover:text-red-700 font-medium"
                                    >
                                        Remove
                                    </button>
                                </div>
                            ))}
                        </div>
                        <div className="mt-8 bg-white rounded-lg shadow-sm p-6">
                            <div className="flex justify-between items-center mb-6">
                                <span className="text-xl font-semibold">Total</span>
                                <span className="text-2xl font-bold text-blue-600">
                                    ${((cart?.totalCents || 0) / 100).toFixed(2)}
                                </span>
                            </div>
                            <button className="w-full py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors">
                                Proceed to Checkout
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}
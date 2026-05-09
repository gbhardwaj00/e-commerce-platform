'use client'

import {useEffect, useState} from "react";
import {OrderResponse} from "@/lib/types/api";
import {useRouter, useParams} from "next/navigation";
import {apiFetch} from "@/lib/api";


export default function OrderDetailPage() {
    const params = useParams();
    const [order, setOrder] = useState<OrderResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("")
    const router = useRouter();
    const orderId = params.orderId as string;

    useEffect(() => {
        const token = localStorage.getItem("token");
        if(!token) {
            router.push("/login");
            return;
        }

        if (!orderId) {
            console.error("orderId is undefined");
            setError("Invalid order ID");
            setLoading(false);  // ← Add this!
            return;
        }

        async function getCart() {
            try {
                const response = await apiFetch<OrderResponse>(`/api/v1/orders/${orderId}`, {
                    token: token!,
                })
                setOrder(response);
            } catch (e: any) {
                let errorMsg = "Failed receiving Order Summary";
                try {
                    const parsed = JSON.parse(e.message);
                    errorMsg = parsed.message || errorMsg;
                }
                catch {
                    errorMsg = e.message || errorMsg;
                }
                setError(errorMsg);
            } finally {
                setLoading(false);
            }
        }
        getCart();
    }, [orderId]);

    if (loading) return <div className="max-w-7xl mx-auto p-8">Loading order...</div>
    if (error) return <div className="max-w-7xl mx-auto p-8 text-red-600">{error}</div>;

    return (
        <div className="min-h-screen bg-gray-50 px-4">
            <div className="max-w-4xl mx-auto py-12">
                <div className="bg-white rounded-lg shadow-sm p-8">
                    <div className="text-center mb-8">
                        <div className="text-6xl mb-4">✅</div>
                        <h1 className="text-3xl font-bold text-gray-900 mb-2">
                            Order Confirmed!
                        </h1>
                        <p className="text-gray-600">
                            Order ID: <span className="font-mono">{order?.orderId}</span>
                        </p>
                        <p className="text-sm text-gray-500 mt-2">
                            {new Date(order?.createdAt || '').toLocaleDateString()}
                        </p>
                    </div>

                    <div className="mb-6">
                        <h2 className="text-xl font-semibold mb-4">Items Ordered</h2>
                        <div className="divide-y">
                            {
                                order?.items.map(item => (
                                    <div key={item.productId} className="py-3 flex justify-between">
                                        <div>
                                            <p className="font-medium">{item.productTitle}</p>
                                            <p className="text-sm text-gray-600">
                                                ${(item.unitPriceCents / 100).toFixed(2)} × {item.quantity}
                                            </p>
                                        </div>
                                        <p className="font-semibold">
                                            ${(item.lineTotalCents / 100).toFixed(2)}
                                        </p>
                                    </div>
                                ))
                            }
                        </div>
                    </div>

                    <div className="border-t pt-4 mb-6">
                        <div className="flex justify-between text-xl font-bold">
                            <span>Total</span>
                            <span className="text-blue-600">
                            ${((order?.totalCents || 0) / 100).toFixed(2)}
                        </span>
                        </div>
                    </div>

                    <button
                        onClick={() => router.push("/products")}
                        className="w-full py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        Continue Shopping
                    </button>
                </div>
            </div>
        </div>
    );
}
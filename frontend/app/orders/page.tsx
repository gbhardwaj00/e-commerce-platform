'use client'

import {useState, useEffect} from "react";
import {useRouter} from "next/navigation";
import {apiFetch} from "@/lib/api";
import {OrderPage, OrderSummary} from "@/lib/types/api";

export default function OrdersPage() {
    const [orders, setOrders] = useState<OrderSummary[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("")
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const router = useRouter();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            router.push("/login");
            return;
        }

        async function fetchOrders() {
            try {
                const response = await apiFetch<OrderPage>(`/api/v1/orders?page=${page}&size=3`, {
                    token: token!
                })
                setOrders(response.items);
                setTotalPages(response.totalPages);
            } catch (e: any) {
                let errorMsg = "Failed to get orders";
                try {
                    let parsed = JSON.parse(e.message);
                    errorMsg = parsed.message || errorMsg;
                } catch {
                    errorMsg = e.message || errorMsg;
                }
                setError(errorMsg);
            } finally {
                setLoading(false);
            }
        }

        fetchOrders();
    }, [page]);

    if (loading) return <div className="max-w-7xl mx-auto p-8">Loading orders...</div>;
    if (error) return <div className="max-w-7xl mx-auto p-8 text-red-600">{error}</div>;

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-6xl mx-auto px-4 py-12">
                <h1 className="text-3xl font-bold mb-8">My Orders</h1>

                {orders.length === 0 ? (
                        <div className="bg-white rounded-lg shadow-sm p-16 text-center">
                            <div className="text-6xl mb-4">📦</div>
                            <h2 className="text-2xl font-semibold text-gray-800 mb-3">
                                No orders yet
                            </h2>
                            <p className="text-gray-600 mb-8">
                                Start shopping to see your orders here
                            </p>
                            <a
                                href="/products"
                                className="inline-block px-8 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors"
                            >
                                Browse Products
                            </a>
                        </div>
                    ) :
                    (<>
                            <div className="space-y-4">
                                {orders.map(order => (
                                    <div key={order.orderId}
                                         className="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow cursor-pointer"
                                         onClick={() => router.push(`/orders/${order.orderId}`)}
                                    >
                                        <div className="flex justify-between items-start">
                                            <div>
                                                <p className="text-sm text-gray-600">
                                                    Order Id: <span className="font-mono">{order.orderId}</span>
                                                </p>
                                                <p className="text-sm text-gray-600 mt-1">
                                                    {new Date(order.createdAt).toLocaleDateString('en-US', {
                                                        year: 'numeric',
                                                        month: 'long',
                                                        day: 'numeric'
                                                    })}
                                                </p>
                                                <span
                                                    className="inline-block mt-2 px-3 py-1 bg-green-100 text-green-800 text-sm rounded-full">
                                            {order.status}
                                        </span>
                                            </div>
                                            <div className="text-right">
                                                <p className="text-2xl font-bold text-blue-600">
                                                    ${(order.totalCents / 100).toFixed(2)}
                                                </p>
                                                <p className="text-sm text-gray-500 mt-1">{order.currency}</p>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>

                            {totalPages > 1 && (
                                <div className="flex justify-center gap-4 mt-8">
                                    <button disabled={page === 0}
                                            onClick={() => setPage(page - 1)}
                                            className="px-4 py-2 bg-blue-600 text-white rounded disabled:bg-gray-300">
                                        Previous
                                    </button>
                                    <button disabled={page >= totalPages - 1}
                                            onClick={() => setPage(page + 1)}
                                            className="px-4 bg-blue-600 text-white rounded disabled:bg-gray-300">
                                        Next
                                    </button>
                                    <span className="py-2"> Page {page + 1} of {totalPages}</span>
                                </div>
                            )}
                        </>
                    )
                }
            </div>
        </div>
    );
}
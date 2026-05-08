'use client'

import {useState, useEffect} from "react";
import {apiFetch} from "@/lib/api";
import {OrderResponse} from "@/lib/types/api";
import {useRouter} from "next/navigation";

export default function CheckoutPage() {
    const [loading, setLoading] = useState(true);
    const[order, setOrder] = useState<OrderResponse | null>(null);
    const [error, setError] = useState("");
    const router = useRouter();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            router.push("/login");
            return;
        }
        async function processCheckout() {
            try {
                const response = await apiFetch<OrderResponse>(`/api/v1/orders/checkout`, {
                    method: 'POST',
                    token: token!
                })
                if (response.items.length === 0) {
                    setError("Cannot checkout an empty cart");
                }
                setOrder(response);
            } catch (e: any) {
                console.error(e.message);
                let errorMsg = "Error checking out";
                try {
                    let parsed = JSON.parse(e.message);
                    errorMsg = parsed.message || errorMsg;
                } catch {
                    errorMsg = e.message || errorMsg;
                }
                setError((errorMsg));
            } finally {
                setLoading(false);
            }
        }
        processCheckout();
    }, []);

    if (loading) return <div className="max-w-7xl mx-auto p-8">Loading...</div>;
    if (error) return <div className="max-w-7xl mx-auto p-8">{error}</div>;

    return (
        <>
        </>
    );
}
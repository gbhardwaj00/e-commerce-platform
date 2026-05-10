'use client'

import {useState} from "react";
import {useRouter} from "next/navigation";
import {apiFetch} from "@/lib/api";
import {AuthResponse} from "@/lib/types/api";
import {useAuth} from "@/contexts/AuthContext";

export default function LoginPage() {
    const {login} = useAuth();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const router = useRouter();

    async function handleSubmit() {
        setError("");
        try {
            const response: AuthResponse = await apiFetch<AuthResponse>("/api/v1/auth/login", {
                method: "POST",
                body: {email, password}
            });
            login(response.token);
            router.push("/products")
        } catch (e: any) {
            let errorMsg = "Login failed";

            if (e.message) {
                try {
                    const parsed = JSON.parse(e.message);
                    const fieldErrors = parsed.fieldErrors;
                    if (fieldErrors && Object.keys(fieldErrors).length > 0) {
                        const errors = Object.values(fieldErrors);
                        errorMsg = errors.join(', ');
                    } else if (parsed.message) {
                        errorMsg = parsed.message;
                    }
                } catch {
                    errorMsg = e.message;
                }
            }

            setError(errorMsg);
        }
    }

    return (
        <>
            <main className="flex items-center justify-center min-h-screen bg-gray-50 text-center">
                <form onSubmit={(e) => {
                    e.preventDefault();
                    handleSubmit();
                }}
                      className="bg-white shadow-md max-w-md p-8 w-full rounded-lg">
                    {error && <p className="text-red-600 text-sm mb-4">{error}</p>}
                    <h1 className="text-2xl font-semibold mb-2">Login</h1>
                    <input type="email" className="border-gray-300 border-2 w-full p-2 mb-4 rounded-md"
                           onChange={(e) => setEmail(e.target.value)} value={email}
                           placeholder="Email"/>
                    <input type="password" className="border-gray-300 border-2 w-full p-2 mb-4 rounded-md"
                           onChange={(e) => setPassword(e.target.value)} value={password}
                           placeholder="Password"/>
                    <button type="submit"
                            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors w-full">Submit
                    </button>
                    <p className="text-sm mt-4">
                        Don't have an account? <a href="/register" className="text-blue-600 hover:underline">Register</a>
                    </p>
                </form>
            </main>
        </>
    );
}
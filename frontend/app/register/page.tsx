'use client'

import {useState} from "react";
import {useRouter} from "next/navigation";
import {apiFetch} from "@/lib/api";

export default function RegisterPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const router = useRouter();

    async function handleSubmit() {
        setError("");
        try {
            await apiFetch("/api/v1/auth/register", {
                method: "POST",
                body: {email, password}
            });
            router.push("/login")
        } catch (e: any) {
            let errorMsg = "Registration failed";

            if (e.message) {
                try {
                    const parsed = JSON.parse(e.message);
                    const fieldErrors = parsed.fieldErrors;
                    if (fieldErrors && Object.keys(fieldErrors).length > 0) {
                        errorMsg = Object.values(fieldErrors)[0] as string;
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
                    <h1 className="text-2xl font-semibold mb-2">Register</h1>
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
                        Already have an account? <a href="/login" className="text-blue-600 hover:underline">Login</a>
                    </p>
                </form>
            </main>
        </>
    );
}
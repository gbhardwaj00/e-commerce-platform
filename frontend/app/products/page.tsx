'use client'

import {useEffect, useState} from "react";
import {apiFetch} from "@/lib/api";
import {Product, ProductPage} from "@/lib/types/api";
import {useRouter} from "next/navigation";

export default function ProductsPage() {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [searchInput, setSearchInput] = useState('');
    const [search, setSearch] = useState('');
    const [successMsg, setSuccessMsg] = useState("")
    const router = useRouter();

    useEffect(() => {
        async function fetchProducts() {
            try {
                const response = await apiFetch<ProductPage>(`/api/v1/products?page=${page}&size=9` + (search ? `&query=${encodeURIComponent(search)}` : ''));
                setProducts(response.items);
                setTotalPages(response.totalPages);
            } catch (e) {
                console.error(e);
            } finally {
                setLoading(false);
            }
        }

        fetchProducts();
    }, [page, search]);


    async function addItemToCart(productId: string) {
        const token = localStorage.getItem("token");
        if(!token) {
            router.push("/login");
            return;
        }
        try {
            await apiFetch(`/api/v1/carts/items`, {
                method: 'POST',
                token: token!,
                body: {
                    productId,
                    quantity: 1
                }
            })
            setSuccessMsg("Item added to cart!");
            setTimeout(() => setSuccessMsg(""), 3000);
        } catch (e: any) {
            console.error("Failed to add to cart:", e);
            let errorMsg = "Failed to add item";
            try {
                const parsed = JSON.parse(e.message);
                errorMsg = parsed.message || errorMsg;
            } catch {
                errorMsg = e.message || errorMsg;
            }
            alert(errorMsg);
        }
    }

    if (loading) return <div className="max-w-7xl mx-auto p-8">Loading...</div>;

    return (
        <main className="max-w-7xl mx-auto p-8 text-center">
            <h1 className="text-3xl font-bold mb-8">Products</h1>
            <div className="mb-6 flex gap-2">
                <input type="text" value={searchInput}
                       onChange={(e) => setSearchInput(e.target.value)}
                       placeholder="Search products..."
                       className="flex-1 border-2 border-gray-300 rounded-md p-2"/>
                <button
                    onClick={() => {
                        setSearch(searchInput);
                        setPage(0);
                    }}
                    className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">
                    Search
                </button>
            </div>
            {successMsg && (
                <div className="bg-green-100 text-green-800 p-3 rounded mb-4">
                    {successMsg}
                </div>
            )}
            {products.length === 0 ? (
                <div className="text-center py-20 text-gray-600">
                    No products found{search && ` for "${search}"`}
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {products.map(product => (
                        <div key={product.id}
                             className="border rounded p-4 flex flex-col">
                            <h2 className="font-semibold mb-2">{product.title}</h2>
                            <p className="mb-4">${(product.priceCents / 100).toFixed(2)}</p>
                            <button
                                className="w-full bg-blue-600 py-2 px-4 text-white rounded mt-auto hover:bg-blue-700 transition-colors"
                                onClick={() => addItemToCart(product.id)}
                            >
                                Add to Cart
                            </button>
                        </div>
                    ))}
                </div>
            )}
            {products.length > 0 && (
                <div className="flex justify-center gap-4 mt-8">
                    <button disabled={page === 0} onClick={() => setPage(page - 1)}
                            className="px-4 py-2 bg-blue-600 text-white rounded disabled:bg-gray-300">
                        Previous
                    </button>
                    <button disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}
                            className="px-4 py-2 bg-blue-600 text-white rounded disabled:bg-gray-300">
                        Next
                    </button>
                    <span className="py-2">Page {page + 1} of {totalPages}</span>
                </div>
            )}
        </main>
    );
}
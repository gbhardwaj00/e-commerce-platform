interface Product {
    id: string;
    title: string;
    description: string;
    priceCents: number;
    currency: string;
    quantityAvailable: number;
}

export default async function Home() {
    const res = await fetch("http://localhost:8080/api/v1/products");
    const data = await res.json();
    const productsArray: Product[] = data.items;

    return (
        <main className="min-h-screen bg-gray-50 p-8">
            <h1 className="text-3xl font-bold mb-6">E-Commerce Application</h1>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {productsArray.map(p =>
                    <div key={p.id} className="rounded-lg border border-gray-200 bg-white p-5 shadow-md">
                        <h2 className="text-lg font-semibold text-gray-900">{p.title}</h2>
                        <p className="mt-1 text-sm text-gray-500">{p.description}</p>
                        <p className="mt-3 text-2xl font-bold text-indigo-600">
                            ${(p.priceCents / 100).toFixed(2)}
                        </p>
                        <button className="mt-4 w-full rounded-md bg-indigo-600 py-2 text-sm font-medium text-white hover:bg-indigo-700">
                            Add to Cart
                        </button>
                    </div>
                )}
            </div>
        </main>
    );
}

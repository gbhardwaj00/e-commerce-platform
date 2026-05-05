export default function Home() {
    const features = [
        {
            emoji: "🚚",
            title: "Free Shipping",
            description: "On orders over $50"
        },
        {
            emoji: "🔒",
            title: "Secure Checkout",
            description: "Your data is protected"
        },
        {
            emoji: "↩️",
            title: "Easy Returns",
            description: "30-day return policy"
        }
    ];
    return (
        <main>
            <section className="flex flex-col justify-center items-center bg-linear-to-r from-blue-600 to-purple-600 h-[calc(100vh-60px)] text-white">
                <h1 className="text-6xl font-bold mb-4">GetItNow</h1>
                <p className="text-xl mb-8">Discover amazing products delivered to your door.</p>
                <a href="/products" className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 hover:shadow-lg transition-all">Browse Products</a>
            </section>
            <section className="py-16 bg-gray-50">
                <div className="max-w-6xl mx-auto px-4">
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        {features.map(feature => (
                            <div key={feature.title} className="text-center">
                                <div className="text-5xl mb-4">{feature.emoji}</div>
                                <h3 className="text-xl font-semibold mb-2">{feature.title}</h3>
                                <p className="text-gray-600">{feature.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>
        </main>
    );
}

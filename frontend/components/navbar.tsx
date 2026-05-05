'use client'

export default function NavBar() {
    return (
        <nav className="bg-white shadow-md py-4">
            <div className="flex justify-between items-center max-w-7xl mx-auto px-4">
                <div>
                    <a className="text-xl font-bold text-blue-600" href="/">GetItNow</a>
                </div>
                <div>
                    <a className="hover:text-blue-600 font-medium mr-4" href="/products">Products</a>
                    <a className="hover:text-blue-600 font-medium mr-4" href="/cart">Cart</a>
                    <a className="bg-blue-600 text-white px-4 py-2 rounded-md mr-2" href="/login">Login</a>
                    <a className="bg-blue-600 text-white px-4 py-2 rounded-md" href="/register">Register</a>
                </div>
            </div>
        </nav>
    );
}
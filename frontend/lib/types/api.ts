export type Product = {
    id: string;
    title: string;
    description: string | null;
    priceCents: number;
    currency: string;
    quantityAvailable: number;
    createdAt: string;
    updatedAt: string;
};

export type ProductCreateRequest = {
    title: string;
    description: string | null;
    priceCents: number;
    currency: string;
    quantityAvailable: number;
};

export type ProductUpdateRequest = {
    title: string;
    description: string | null;
    priceCents: number;
    currency: string;
    quantityAvailable: number;
};

export type AuthResponse = {
    token: string;
    userId: string;
    email: string;
    role: string;
};

export type UserResponse = {
    id: string;
    email: string;
    role: string;
    createdAt: string;
};

export type LoginRequest = {
    email: string;
    password: string;
};

export type RegisterRequest = {
    email: string;
    password: string;
};

export type CartItemView = {
    productId: string;
    quantity: number;
};

export type CartItemDetailed = {
    productId: string;
    title: string;
    priceCents: number;
    currency: string;
    quantity: number;
    lineTotalCents: number;
};

export type CartDetailedView = {
    cartId: string;
    items: CartItemDetailed[];
    totalCents: number;
    currency: string;
};

export type AddCartItemRequest = {
    productId: string;
    quantity: number;
};

export type UpdateCartItemRequest = {
    newQuantity: number;
};

export type OrderItemResponse = {
    productId: string;
    productTitle: string;
    unitPriceCents: number;
    currency: string;
    quantity: number;
    lineTotalCents: number;
};

export type OrderResponse = {
    orderId: string;
    cartId: string;
    status: string;
    currency: string;
    totalCents: number;
    createdAt: string;
    items: OrderItemResponse[];
};

export type OrderSummary = {
    orderId: string;
    cartId: string;
    status: string;
    currency: string;
    totalCents: number;
    createdAt: string;
};

export type ErrorResponse = {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
    fieldErrors: Record<string, string>;
};

export type PageResponse<T> = {
    items: T[];
    page: number;
    size: number;
    totalItems: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
    sort: string[];
};

export type ProductPage = PageResponse<Product>;
export type OrderPage = PageResponse<OrderSummary>;

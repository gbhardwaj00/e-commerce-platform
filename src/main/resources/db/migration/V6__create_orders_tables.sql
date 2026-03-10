CREATE TABLE orders (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    total_cents INTEGER NOT NULL CHECK (total_cents>= 0),
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_orders_created_at ON orders (created_at DESC);

CREATE TABLE order_items (
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE ,
    product_id UUID NOT NULL REFERENCES catalog_products(id),
    quantity INTEGER NOT NULL check ( quantity > 0 ),

    -- Snapshot fields(freeze purchase details)
    unit_price_cents INTEGER NOT NULL CHECK (unit_price_cents >= 0),
    currency VARCHAR(3) NOT NULL,
    product_title TEXT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE TABLE products (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    price_cents INTEGER NOT NULL CHECK ( price_cents >= 0 ),
    currency CHAR(3) NOT NULL,
    quantity_available INTEGER NOT NULL DEFAULT 0 CHECK ( quantity_available >= 0 ),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_products_title ON products (title);
CREATE INDEX idx_products_created_at ON products (created_at DESC);

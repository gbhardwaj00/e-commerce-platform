ALTER TABLE products RENAME TO catalog_products;

-- renaming indices too
ALTER INDEX idx_products_title RENAME TO idx_catalog_products_title;
ALTER INDEX idx_products_created_at RENAME TO idx_catalog_products_created_at;
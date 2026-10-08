-- Product search uses: upper(title) LIKE upper('%term%'), which a btree index cannot serve.
-- A trigram GIN index on the same expression lets Postgres avoid a full table scan.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_catalog_products_title_trgm
    ON catalog_products USING gin (upper(title) gin_trgm_ops);

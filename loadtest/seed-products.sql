-- Seeds 100,000 products (titles prefixed SEED-) for search benchmarking.
-- "quantum" appears in ~0.5% of titles so a search has to look through the whole table.
INSERT INTO catalog_products (id, title, description, price_cents, currency, quantity_available, created_at, updated_at)
SELECT gen_random_uuid(),
       'SEED-' || (ARRAY['Ergonomic','Wireless','Compact','Premium','Rugged','Smart','Classic','Portable','Modular','Vintage'])[1 + (g % 10)]
         || ' ' || (ARRAY['Keyboard','Monitor','Headset','Charger','Backpack','Lamp','Router','Speaker','Webcam','Dock'])[1 + ((g / 10) % 10)]
         || CASE WHEN g % 200 = 0 THEN ' quantum' ELSE '' END
         || ' #' || g,
       'seed product', 1000 + (g % 5000), 'USD', 100, now(), now()
FROM generate_series(1, 100000) g;
ANALYZE catalog_products;

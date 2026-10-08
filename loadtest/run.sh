#!/usr/bin/env bash
# Usage: ./loadtest/run.sh   (stack must be up: docker compose up -d)
set -euo pipefail
API=http://localhost:8080/api/v1
EMAIL=loadtest-admin@example.com
PW='LoadTest123!'
RUN_ID=$(date +%s)

curl -s -o /dev/null -X POST $API/auth/register -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PW\"}" || true
docker exec ecommerce-postgres psql -U ecommerce -d ecommerce -c \
  "UPDATE users SET role='ADMIN' WHERE email='$EMAIL';"

RUN_ID=$RUN_ID k6 run "$(dirname "$0")/load-test.js" || true

echo "---- DB verification ----"
docker exec ecommerce-postgres psql -U ecommerce -d ecommerce -c \
  "SELECT title, quantity_available AS remaining_stock FROM catalog_products WHERE title='LOADTEST-CHECKOUT-$RUN_ID';" \
  -c "SELECT count(*) AS orders_for_product FROM order_items oi JOIN catalog_products p ON p.id=oi.product_id WHERE p.title='LOADTEST-CHECKOUT-$RUN_ID';"

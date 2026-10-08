#!/usr/bin/env bash
# Usage: ./loadtest/deadlock.sh   (admin user must exist; run.sh creates it)
set -euo pipefail
PG="docker exec ecommerce-postgres"
BEFORE=$(docker logs ecommerce-postgres 2>&1 | grep -c "deadlock detected" || true)
RUN_ID=$(date +%s) k6 run "$(dirname "$0")/deadlock-test.js" 2>&1 | grep -E 'checkout_|iterations\.' || true
AFTER=$(docker logs ecommerce-postgres 2>&1 | grep -c "deadlock detected" || true)
echo "Postgres deadlocks detected during run: $((AFTER-BEFORE))"

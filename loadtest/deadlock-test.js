// Concurrent multi-product checkouts where half the users add P1 then P2 and the
// other half add P2 then P1. Opposite lock orders are the classic deadlock setup.
import http from 'k6/http';
import { Counter } from 'k6/metrics';

const BASE = __ENV.BASE_URL || 'http://localhost:8080/api/v1';
const ADMIN_EMAIL = __ENV.ADMIN_EMAIL || 'loadtest-admin@example.com';
const PASSWORD = 'LoadTest123!';
const RUN_ID = __ENV.RUN_ID || String(Date.now());

const ok = new Counter('checkout_success');
const serverErrors = new Counter('checkout_5xx');
const other = new Counter('checkout_other');

const json = { headers: { 'Content-Type': 'application/json' } };
const authed = (t) => ({ headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${t}` } });

export const options = {
  scenarios: {
    opposite_order: { executor: 'shared-iterations', vus: 100, iterations: 1000, maxDuration: '3m' },
  },
};

export function setup() {
  const login = http.post(`${BASE}/auth/login`, JSON.stringify({ email: ADMIN_EMAIL, password: PASSWORD }), json);
  const t = login.json('token');
  const mk = (n) => http.post(`${BASE}/products`, JSON.stringify({
    title: `LOADTEST-DEADLOCK-${RUN_ID}-${n}`, description: 'x', priceCents: 100, currency: 'USD', quantityAvailable: 1000000,
  }), authed(t)).json('id');
  return { p1: mk(1), p2: mk(2) };
}

export default function (d) {
  const email = `dl-${RUN_ID}-${__VU}-${__ITER}@example.com`;
  http.post(`${BASE}/auth/register`, JSON.stringify({ email, password: PASSWORD }), json);
  const token = http.post(`${BASE}/auth/login`, JSON.stringify({ email, password: PASSWORD }), json).json('token');
  const order = __VU % 2 === 0 ? [d.p1, d.p2] : [d.p2, d.p1];
  for (const id of order) {
    http.post(`${BASE}/carts/items`, JSON.stringify({ productId: id, quantity: 1 }), authed(token));
  }
  const r = http.post(`${BASE}/orders/checkout`, null, authed(token));
  if (r.status === 200 || r.status === 201) ok.add(1);
  else if (r.status >= 500) serverErrors.add(1);
  else other.add(1);
}

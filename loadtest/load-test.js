import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

const BASE = __ENV.BASE_URL || 'http://localhost:8080/api/v1';
const ADMIN_EMAIL = __ENV.ADMIN_EMAIL || 'loadtest-admin@example.com';
const PASSWORD = 'LoadTest123!';
const STOCK = Number(__ENV.STOCK || 500);
const CHECKOUT_ATTEMPTS = Number(__ENV.ATTEMPTS || 1000);
const RUN_ID = __ENV.RUN_ID || String(Date.now());

const checkoutSuccess = new Counter('checkout_success');
const cartRejected = new Counter('rejected_at_cart_out_of_stock');
const checkoutRejected = new Counter('checkout_rejected_out_of_stock');
const checkoutUnexpected = new Counter('checkout_unexpected_error');

const json = { headers: { 'Content-Type': 'application/json' } };
const authed = (token) => ({
  headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
});

export const options = {
  scenarios: {
    browse: {
      executor: 'ramping-vus',
      exec: 'browse',
      stages: [
        { duration: '20s', target: 50 },
        { duration: '40s', target: 50 },
        { duration: '10s', target: 0 },
      ],
    },
    checkout_race: {
      executor: 'shared-iterations',
      exec: 'checkoutRace',
      vus: 100,
      iterations: CHECKOUT_ATTEMPTS,
      maxDuration: '3m',
      startTime: '5s',
    },
  },
  thresholds: {
    'http_req_failed{scenario:browse}': ['rate<0.01'],
    'http_req_duration{scenario:browse}': ['p(95)<500'],
    checkout_unexpected_error: ['count==0'],
    checkout_success: [`count==${STOCK}`],
  },
};

if (__ENV.ONLY === 'checkout') {
  delete options.scenarios.browse;
  delete options.thresholds['http_req_failed{scenario:browse}'];
  delete options.thresholds['http_req_duration{scenario:browse}'];
  options.scenarios.checkout_race.startTime = '0s';
}

export function setup() {
  const login = http.post(`${BASE}/auth/login`,
    JSON.stringify({ email: ADMIN_EMAIL, password: PASSWORD }), json);
  if (login.status !== 200) throw new Error(`admin login failed: ${login.status} ${login.body}`);
  const token = login.json('token');

  const create = (title, qty) => http.post(`${BASE}/products`, JSON.stringify({
    title, description: 'load test', priceCents: 1999, currency: 'USD', quantityAvailable: qty,
  }), authed(token));

  for (let i = 0; i < 30; i++) create(`LOADTEST-BROWSE-${RUN_ID}-${i}`, 100000);

  const p = create(`LOADTEST-CHECKOUT-${RUN_ID}`, STOCK);
  if (p.status !== 200 && p.status !== 201) throw new Error(`product create failed: ${p.status} ${p.body}`);
  return { productId: p.json('id') };
}

export function browse() {
  const list = http.get(`${BASE}/products?page=0&size=20`);
  check(list, { 'list 200': (r) => r.status === 200 });
  const search = http.get(`${BASE}/products?query=LOADTEST&page=0&size=10`);
  check(search, { 'search 200': (r) => r.status === 200 });
  sleep(1);
}

export function checkoutRace(data) {
  const email = `lt-${RUN_ID}-${__VU}-${__ITER}@example.com`;
  http.post(`${BASE}/auth/register`, JSON.stringify({ email, password: PASSWORD }), json);
  const login = http.post(`${BASE}/auth/login`, JSON.stringify({ email, password: PASSWORD }), json);
  if (login.status !== 200) { checkoutUnexpected.add(1); return; }
  const token = login.json('token');

  const add = http.post(`${BASE}/carts/items`,
    JSON.stringify({ productId: data.productId, quantity: 1 }), authed(token));
  if (add.status === 400) { cartRejected.add(1); return; } // out of stock caught at cart stage
  if (add.status !== 200 && add.status !== 201) { checkoutUnexpected.add(1); return; }

  const co = http.post(`${BASE}/orders/checkout`, null, authed(token));
  if (co.status === 200 || co.status === 201) checkoutSuccess.add(1);
  else if (co.status === 400) checkoutRejected.add(1);
  else checkoutUnexpected.add(1);
}

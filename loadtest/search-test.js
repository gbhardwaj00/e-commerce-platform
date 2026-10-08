import http from 'k6/http';
import { check } from 'k6';
const BASE = __ENV.BASE_URL || 'http://localhost:8080/api/v1';
export const options = {
  vus: 20, duration: '20s',
  thresholds: { http_req_failed: ['rate<0.01'] },
};
export default function () {
  const r = http.get(`${BASE}/products?query=quantum&page=0&size=20`);
  check(r, { 'search 200': (x) => x.status === 200 });
}

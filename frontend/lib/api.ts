export function getApiBaseUrl(): string {
    const raw:string = process.env.NEXT_PUBLIC_API_URL ?? `http://localhost:8080`;
    return raw.replace(/\/+$/, "");
}

export function apiUrl(path: string) : string {
    const p = path.startsWith("/") ? path : `/${path}`;
    return getApiBaseUrl() + p;
}

type ApiFetchOptions = {
    method?: string;
    body?: unknown;
    token?: string;
    /** Pass through to `fetch` (e.g. `no-store` in Next.js server components). */
    cache?: RequestCache;
};

export async function apiFetch<T>(path: string, options?: ApiFetchOptions): Promise<T> {
    const headers = new Headers();
    headers.set("Accept", "application/json");
    if(options?.body !== undefined) {
        headers.set("Content-Type", "application/json");
    }
    if (options?.token) {
        headers.set("Authorization", `Bearer ${options.token}`);
    }
    const res = await fetch(apiUrl(path), {
        method: options?.method ?? "GET",
        headers,
        body: options?.body !== undefined ? JSON.stringify(options.body) : undefined,
        cache: options?.cache,
    });
    if(!res.ok) {
        const text = await res.text();
        throw new Error(text || `HTTP ${res.status}`);
    }
    return (await res.json()) as T;
}
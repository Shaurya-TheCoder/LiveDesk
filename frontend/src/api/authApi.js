import { apiFetch } from "./client.js";

export function login(email, rawPassword) {
    return apiFetch("/api/auth/login", {
        method: "POST",
        skipAuth: true,
        body: JSON.stringify({
            email,
            rawPassword
        })
    });
}
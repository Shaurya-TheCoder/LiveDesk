import { apiFetch } from "./client.js";

export function login(email, rawPassword) {
    return apiFetch("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
            email,
            rawPassword
        })
    });
}
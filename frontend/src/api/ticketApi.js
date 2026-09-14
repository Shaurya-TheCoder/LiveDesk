import { apiFetch } from "./client.js";

export function createTicket(message) {
    return apiFetch("/api/v1/tickets", {
        method: "POST",
        body: JSON.stringify({
            message
        })
    });
}

export function getTicket(ticketId, sessionToken) {
    return apiFetch(`/api/v1/tickets/${ticketId}`, {
        headers: {
            "Session-Token": sessionToken
        }
    });
}

export function getTicketMessage(ticketId, sessionToken, page=0, size=20){
    return apiFetch(
        `/api/v1/tickets/${ticketId}/messages?page=${page}&size=${size}`,
        {
            headers: {
                "Session-Token": sessionToken
            }
        }
    );
}

export function resolveTicket(ticketId, accessToken) {
    return apiFetch(`/api/v1/tickets/${ticketId}/resolve`, {
        method: "PATCH",
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    });
}
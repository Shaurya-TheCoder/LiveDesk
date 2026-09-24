import { apiFetch } from "./client.js";

export function createTicket(subject, message) {
    return apiFetch("/api/v1/tickets", {
        method: "POST",
        body: JSON.stringify({
            subject,
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

export function getTicketMessage(
    ticketId,
    { sessionToken, token },
    page = 0,
    size = 20
) {
    const headers = {};

    if (sessionToken) {
        headers["Session-Token"] = sessionToken;
    }

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    return apiFetch(
        `/api/v1/tickets/${ticketId}/messages?page=${page}&size=${size}`,
        {
            headers
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

export function getAgentPresence(ticketId, sessionToken){
    return apiFetch(`/api/v1/tickets/${ticketId}/agent-presence`,
    {
        headers: {
            "Session-Token": sessionToken
        }    
    });
}

export function getAssignedTickets(jwtToken){
    return apiFetch('/api/v1/tickets/assigned',
    {
        headers: {
            "Authorization": `Bearer ${jwtToken}`
        }
    });
}

export function getResolvedTickets(jwtToken){
    return apiFetch('/api/v1/tickets/resolved',
    {
        headers: {
            "Authorization": `Bearer ${jwtToken}`
        }
    });
}

export function recoverTicket(recoveryCode){
    return apiFetch('/api/v1/tickets/recover', {
        method: "POST",
        body: JSON.stringify({
            recoveryCode
        })
    });
}

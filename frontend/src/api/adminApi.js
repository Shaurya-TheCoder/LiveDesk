import { apiFetch } from "./client.js";

/**
 * Helper to build Bearer token headers for Admin requests.
 */
function authHeader(token) {
  return {
    Authorization: `Bearer ${token}`,
  };
}

/**
 * Fetch overall system-wide ticket statistics for the Admin Dashboard.
 * Returns metrics such as totalCreated, queued, escalated, and closed.
 */
export function getAdminSystemStats(token) {
  return apiFetch("/api/v1/admin/stats", {
    headers: authHeader(token),
  });
}

/**
 * Fetch list of all registered agents.
 */
export function getAllAgents(token) {
  return apiFetch("/api/v1/admin/agents", {
    headers: authHeader(token),
  });
}

/**
 * Fetch specific agent details including current workload metrics.
 */
export function getAgentDetails(agentId, token) {
  return apiFetch(`/api/v1/admin/agents/${agentId}`, {
    headers: authHeader(token),
  });
}

/**
 * Fetch tickets resolved by a specific agent.
 */
export function getAgentResolvedTickets(agentId, token) {
  return apiFetch(
    `/api/v1/admin/agents/${agentId}/resolved-tickets`,
    {
      headers: authHeader(token),
    }
  );
}

export function closeTicket(ticketId, token) {
  return apiFetch(
    `/api/v1/admin/tickets/${ticketId}/close`,
    {
      method: "POST",
      headers: authHeader(token),
    }
  );
}

/**
 * Register/Create a new support agent account.
 */
export function registerAgent(agentData, token) {
  return apiFetch("/api/v1/admin/agents/register", {
    method: "POST",
    headers: {
      ...authHeader(token),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(agentData),
  });
}
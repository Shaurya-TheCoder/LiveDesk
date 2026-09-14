export const WS_DESTINATIONS = {
    chat: (ticketId) => `/topic/chat/${ticketId}`,

    typing: (ticketId) => `/topic/chat/${ticketId}/typing`,

    ticketNotifications: (ticketId) =>
        `/topic/ticket/${ticketId}/notifications`,

    sendChat: (ticketId) =>
        `/app/chat/${ticketId}`,

    sendTyping: (ticketId) =>
        `/app/chat/${ticketId}/typing`
};
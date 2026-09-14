import { Client } from "@stomp/stompjs";

let stompClient = null;

export function connectStomp({
    sessionToken,
    onConnect,
    onError
}) {
    if (stompClient?.active) {
        return stompClient;
    }

    stompClient = new Client({
        brokerURL: "ws://localhost:3030/ws",

        connectHeaders: {
            "Session-Token": sessionToken
        },

        reconnectDelay: 5000,

        debug: (message) => {
            console.log("[STOMP]", message);
        },

        onConnect: (frame) => {
            console.log("STOMP connected:", frame);

            onConnect?.(stompClient);
        },

        onStompError: (frame) => {
            console.error(
                "STOMP broker error:",
                frame.headers["message"],
                frame.body
            );

            onError?.(frame);
        },

        onWebSocketError: (error) => {
            console.error("WebSocket error:", error);

            onError?.(error);
        }
    });

    stompClient.activate();

    return stompClient;
}

export function disconnectStomp() {
    if (!stompClient) {
        return;
    }

    stompClient.deactivate();
    stompClient = null;
}

export function getStompClient() {
    return stompClient;
}
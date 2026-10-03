import { Client } from "@stomp/stompjs";

let stompClient = null;
let heartbeatInterval = null;
const connectListeners = new Set();
const errorListeners = new Set();

export function connectStomp({ sessionToken, token, onConnect, onError }) {
  if (onConnect) connectListeners.add(onConnect);
  if (onError) errorListeners.add(onError);

  // If already active, trigger onConnect immediately if connected
  if (stompClient?.active) {
    if (stompClient.connected && onConnect) {
      onConnect(stompClient);
    }
    return stompClient;
  }

  const connectHeaders = sessionToken
    ? { "Session-Token": sessionToken }
    : token
    ? { Authorization: `Bearer ${token}` }
    : null;

  if (!connectHeaders) {
    throw new Error("No STOMP authentication credential provided");
  }

  stompClient = new Client({
    brokerURL: "ws://localhost:3030/ws",
    connectHeaders,
    reconnectDelay: 5000,

    debug: (message) => {
      console.log("[STOMP]", message);
    },

    onConnect: (frame) => {
      console.log("STOMP connected:", frame);
      // Notify ALL registered connection listeners on initial connect AND reconnects
      connectListeners.forEach((callback) => callback(stompClient, frame));
    },

    onStompError: (frame) => {
      console.error(
        "STOMP broker error:",
        frame.headers["message"],
        frame.body
      );
      errorListeners.forEach((callback) => callback(frame));
    },

    onWebSocketError: (error) => {
      console.error("WebSocket error:", error);
      errorListeners.forEach((callback) => callback(error));
    },
  });

  stompClient.activate();
  return stompClient;
}

// Add a helper to subscribe component callbacks to connection events
export function onStompConnect(callback) {
  connectListeners.add(callback);
  
  // If already connected when listener is attached, trigger immediately
  if (stompClient?.connected) {
    callback(stompClient);
  }

  // Return unsubscribe function for cleanup inside React useEffect
  return () => {
    connectListeners.delete(callback);
  };
}

export function disconnectStomp() {
  if (!stompClient) return;

  stompClient.deactivate();
  stompClient = null;
  connectListeners.clear();
  errorListeners.clear();
}

export function getStompClient() {
  return stompClient;
}

export function startAgentHeartbeat() {
  if (heartbeatInterval) return;

  heartbeatInterval = setInterval(() => {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: "/app/agent/heartbeat",
        body: "",
      });
    }
  }, 10000);
}

export function stopAgentHeartbeat() {
  if (heartbeatInterval) {
    clearInterval(heartbeatInterval);
    heartbeatInterval = null;
  }
}
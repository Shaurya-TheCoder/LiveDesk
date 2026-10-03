import { create } from "zustand";
import { persist } from "zustand/middleware";

const useAuthStore = create(
    persist(
        (set) => ({
            token: null,
            agentId: null,
            email: null,
            role:null,
            isAuthenticated: false,

            setAuth: (authData) => {
                set({
                    token: authData.token,
                    agentId: authData.id,
                    email: authData.email,
                    role : authData.role,
                    isAuthenticated: true
                });
            },

            logout: () => {
                set({
                    token: null,
                    agentId: null,
                    email: null,
                    role : null,
                    isAuthenticated: false
                });
            }
        }),
        {
            name: "livedesk-agent-auth"
        }
    )
);

export default useAuthStore;
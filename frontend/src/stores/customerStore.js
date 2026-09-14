import { create } from "zustand";
import { persist } from "zustand/middleware";

const useCustomerStore = create(
    persist(
        (set) => ({
            ticketId: null,
            sessionToken: null,
            queuePosition: null,

            setTicketSession: ({ ticketId, sessionToken, queuePosition }) => {
                set({
                    ticketId,
                    sessionToken,
                    queuePosition
                });
            },

            clearTicketSession: () => {
                set({
                    ticketId: null,
                    sessionToken: null,
                    queuePosition: null
                });
            }
        }),
        {
            name: "customer-ticket-session"
        }
    )
);

export default useCustomerStore;
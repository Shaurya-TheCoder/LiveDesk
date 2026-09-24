import { create } from "zustand";
import { persist } from "zustand/middleware";

const useCustomerStore = create(
    persist(
        (set) => ({
            ticketId: null,
            sessionToken: null,

            setTicketSession: ({ ticketId, sessionToken}) => {
                set({
                    ticketId,
                    sessionToken,
                });
            },

            clearTicketSession: () => {
                set({
                    ticketId: null,
                    sessionToken: null,
                });
            }
        }),
        {
            name: "customer-ticket-session"
        }
    )
);

export default useCustomerStore;
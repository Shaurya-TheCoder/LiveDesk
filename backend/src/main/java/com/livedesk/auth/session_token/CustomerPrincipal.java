package com.livedesk.auth.session_token;

public class CustomerPrincipal {
    private final Long ticketId;

    public CustomerPrincipal(Long ticketId){
        this.ticketId = ticketId;
    }

    public Long getTicketId() {
        return ticketId;
    }
}

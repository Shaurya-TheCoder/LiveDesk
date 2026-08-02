package com.livedesk.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(

        @NotBlank(message = "First message must not be blank")
        @Size(max = 2000, message = "First message must not exceed 2000 characters")
        String message

) {}

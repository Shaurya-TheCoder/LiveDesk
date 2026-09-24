package com.livedesk.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(

        @NotBlank(message = "First message must not be blank")
        @Size(max = 2000, message = "First message must not exceed 2000 characters")
        String message,

        @NotBlank(message = "Subject must not be Blank")
        @Size(max = 255, message = "Subject should not execeed more than 255 characters")
        String subject

) {}

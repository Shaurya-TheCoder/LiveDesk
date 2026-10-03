package com.livedesk.ticket.dto;

import jakarta.validation.constraints.NotBlank;

public record RecoverTicketRequest(
        @NotBlank String recoveryCode
) {
}

package com.livedesk.ticket;

import com.livedesk.ticket.dto.CreateTicketRequest;
import com.livedesk.ticket.dto.CreateTicketResponse;
import com.livedesk.ticket.dto.GetTicketResponse;
import com.livedesk.ticket.exception.TicketNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }
    @GetMapping("/tickets/{id}")
    public ResponseEntity<GetTicketResponse> getTicket(@PathVariable Long id){
        Ticket ticket = ticketService
                .getTicket(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found: "+id));
        GetTicketResponse response = new GetTicketResponse(
                ticket.getId().orElseThrow(
                        () -> new IllegalStateException("ticket id cannot be null")
                ),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/tickets")
    public ResponseEntity<CreateTicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request){
        Ticket ticket = ticketService.createTicket(request.message());
        CreateTicketResponse response = new CreateTicketResponse(
                ticket.getId().orElseThrow(
                        () -> new IllegalStateException("ticket id cannot be null")
                ),
                null, //sessionToken
                null //queuePosition
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

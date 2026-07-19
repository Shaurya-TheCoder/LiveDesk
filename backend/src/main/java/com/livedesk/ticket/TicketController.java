package com.livedesk.ticket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }
    @GetMapping("/tickets/{id}")
    public Ticket getTicket(@PathVariable Long id){
        return ticketService
                .getTicket(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found: "+id));
    }
    @PostMapping("/tickets")
    public ResponseEntity<Ticket> createTicket(@RequestBody CreateTicketRequest request){
        Ticket ticket = ticketService.createTicket(request.subject());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}

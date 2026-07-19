package com.livedesk.ticket;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TicketRepository {
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, Ticket> store = new ConcurrentHashMap<>();

    public Ticket save(Ticket ticket){
        long newId = idGenerator.incrementAndGet();
        ticket.setId(newId);
        store.put(newId , ticket);

        return ticket;
    }

    public Optional<Ticket> findById(Long id){
        return Optional.ofNullable(store.get(id));
    }
}
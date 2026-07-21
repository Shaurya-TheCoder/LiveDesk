# Architecture

## Layering

LiveDesk follows a standard layered architecture:
Client (HTTP)
↓
Controller   → handles HTTP concerns only (status codes, request/response mapping)
↓
Service      → business logic, orchestration; HTTP-agnostic
↓
Repository   → data access; currently in-memory, will become JPA + PostgreSQL
↓
Storage      → currently ConcurrentHashMap, will become PostgreSQL

**Rule:** each layer only calls the layer directly beneath it. Controllers never call Repositories directly.

## Package-by-Feature

Packages are organized by business capability, not by architectural layer:
com.livedesk.ticket   → everything related to tickets: domain, repository, service,
controller, DTOs, and ticket-specific exceptions
com.livedesk.common   → cross-cutting infrastructure shared across all features
(error response shape, global exception handling)

This scales better than package-by-layer as features multiply (Agent, Message, Auth
will each get their own package, not be scattered across shared `controller/`,
`service/`, `repository/` folders).

## Domain Model Design

`Ticket` is a rich domain model, not an anemic data holder:

- Constructor validates all invariants (non-blank subject, non-null createdAt);
  invalid tickets cannot be constructed.
- `status` cannot be set directly — no `setStatus()` exists. State changes only
  happen through behavior methods (`assign()`, `resolve()`, `close()`), each of
  which enforces which transitions are legal via guard clauses, throwing
  `IllegalStateException` otherwise.
- `id` is assigned exactly once, via a package-private `setId()`, callable only
  by `TicketRepository` (same package) — external code cannot set or reassign it.

## Persistence (current)

`TicketRepository` is in-memory for Phase 1, using:
- `ConcurrentHashMap<Long, Ticket>` for thread-safe storage
- `AtomicLong` for thread-safe, unique id generation

This is a deliberate placeholder. When PostgreSQL is introduced, only
`TicketRepository`'s internals change — `TicketService` and `TicketController`
require no changes, since they depend on the Repository's public contract
(`save`, `findById`), not its implementation.

## Error Handling

Centralized via `GlobalExceptionHandler` (`@RestControllerAdvice`), mapping:

| Exception | HTTP Status | Meaning |
|---|---|---|
| `TicketNotFoundException` | 404 | Requested resource doesn't exist |
| `IllegalArgumentException` | 400 | Client sent invalid input |
| `IllegalStateException` | 409 | Valid input, but conflicts with current resource state |

## DTOs

Controllers never bind untrusted JSON directly onto domain objects. Input/output
shapes use dedicated DTOs (e.g., `CreateTicketRequest`, a Java `record`), keeping
the domain model's invariants fully protected from external input.
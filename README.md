# LiveDesk

A real-time customer support/helpdesk backend, built with Spring Boot, as a learning-focused, production-inspired project.

## Current Status

**Phase 1 complete:** Ticket domain model, in-memory persistence, REST API for ticket creation/retrieval, and global exception handling.

## Tech Stack (so far)

- Java 21
- Spring Boot (Web)
- Maven

_No database yet — Phase 1 intentionally uses in-memory storage to focus on architecture before introducing persistence._

## Running Locally

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:3030`.

## API Endpoints (Phase 1)

| Method | Path | Description |
|---|---|---|
| POST | `/tickets` | Create a new ticket |
| GET | `/tickets/{id}` | Retrieve a ticket by id |

See `docs/api.md` for full request/response details.

## Project Structure
- com.livedesk.ticket   → Ticket domain, repository, service, controller (package-by-feature)
- com.livedesk.common   → Shared infrastructure (error handling, shared DTOs)
- See `architecture.md` for design rationale.

## Roadmap

- Phase 2: Agent authentication (JWT)
- Phase 3: WebSocket real-time messaging
- Phase 4: Routing engine + queue logic
- Phase 5: SLA escalation scheduler
- Phase 6: Presence, typing indicators, reconnect handling
- Phase 7: Testing, hardening
- Phase 8: Observability, deployment, docs
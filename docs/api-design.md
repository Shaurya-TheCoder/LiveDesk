# API Documentation

## POST /tickets

Creates a new ticket. Newly created tickets always start with status `QUEUED`.

**Request body:**
```json
{ "subject": "My login isn't working" }
```

**Success response — `201 Created`:**
```json
{
  "id": 1,
  "subject": "My login isn't working",
  "status": "QUEUED",
  "createdAt": "2026-07-19T10:15:30"
}
```

**Error response — `400 Bad Request`** (blank/missing subject):
```json
{ "message": "subject must not be null or blank" }
```

---

## GET /tickets/{id}

Retrieves a ticket by id.

**Success response — `200 OK`:**
```json
{
  "id": 1,
  "subject": "My login isn't working",
  "status": "QUEUED",
  "createdAt": "2026-07-19T10:15:30"
}
```

**Error response — `404 Not Found`:**
```json
{ "message": "Ticket not found: 999" }
```
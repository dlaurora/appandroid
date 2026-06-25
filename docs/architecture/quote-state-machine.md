# Quote State Machine

## Statuses

```text
DRAFT
SENT
APPROVED
REJECTED
EXPIRED
CANCELLED
```

## Allowed Transitions

| From | To |
| --- | --- |
| `DRAFT` | `SENT` |
| `DRAFT` | `CANCELLED` |
| `SENT` | `APPROVED` |
| `SENT` | `REJECTED` |
| `SENT` | `EXPIRED` |
| `SENT` | `CANCELLED` |
| `APPROVED` | `CANCELLED` |

## Rules

- Invalid transitions are rejected in domain logic.
- Full editing is limited to `DRAFT`.
- `REJECTED` and `EXPIRED` quotes do not silently return to `DRAFT`; users duplicate them when a new draft is needed.
- `APPROVED -> CANCELLED` is allowed only through an explicit user confirmation in the UI.
- No external communication is sent when status changes.
- Automatic expiry by date is not implemented in Phase 4.

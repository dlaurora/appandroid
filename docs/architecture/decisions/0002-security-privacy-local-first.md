# ADR 0002: Security, Privacy, and Local-First Baseline

## Status

Accepted.

## Context

TechQuote may process customer details, contact data, addresses, quotes, amounts, technical reports, photos, exported PDFs, and future backups. The MVP must avoid unnecessary data exposure.

## Decision

TechQuote MVP is offline-first and local-first. The scaffold declares no Android permissions, no Internet permission, no telemetry, no analytics, no advertising SDK, and no external server behavior.

Legal screens, privacy policy display, terms display, app version display, third-party license display, and first-use legal notice are required before publication, but they are deferred until a Settings or Legal UI phase is explicitly accepted.

## Consequences

- Future data, storage, sharing, backup, PDF, import, attachment, dependency, permission, or release work must update security docs.
- Future legal UI must work offline and match the legal drafts.
- Google Play Data Safety must match real behavior and third-party dependency behavior.

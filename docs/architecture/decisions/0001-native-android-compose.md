# ADR 0001: Native Android With Compose

## Status

Accepted.

## Context

TechQuote needs a maintainable Android foundation before business behavior is implemented.

## Decision

Use a native Android app with Kotlin, Jetpack Compose, Material 3, Hilt, Room, and Navigation Compose.

## Consequences

- The UI can be built with Compose-first patterns.
- Dependency injection is available from the start through Hilt.
- Persistence can be added later with Room without changing the selected stack.
- Business features remain deferred until the full plan is documented.

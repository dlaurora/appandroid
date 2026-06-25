---
name: techquote-planning
description: Use when a TechQuote product plan, feature plan, business requirements list, or implementation roadmap is provided and needs to become project documentation, scope boundaries, or implementation steps.
---

# TechQuote Planning

## Core Rule

Convert plans into explicit scope before implementation. Separate business behavior from Android infrastructure.

## Intake Checklist

- Identify the smallest milestone that can compile and be verified.
- Record out-of-scope items explicitly, especially clients, catalog, quotes, reports, PDFs, backups, imports, pricing, integrations, and database schema.
- Treat security, privacy, legal, and release readiness requirements as phase gates, not as permission to implement business behavior.
- Save durable decisions in `docs/architecture/decisions`.
- Save pending product detail in `docs/plans`.
- Update `docs/security`, `docs/legal`, and `docs/release` when a plan changes permissions, storage, sharing, backups, imports, PDFs, dependencies, data collection, or legal claims.
- Do not create Kotlin domain models until the accepted plan names the required concepts and relationships.

## Handoff

When implementation is allowed, create focused tasks with exact files and verification commands. Keep each task independently buildable.

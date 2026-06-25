# Design Principles

## Phase 0 Scope

Phase 0 defines UX guardrails only. It does not implement settings, legal, client, catalog, quote, report, PDF, backup, import, or business screens.

## Phase 1 Scope

Phase 1 implements visual-only Compose screens, reusable UI components, navigation, and previews with safe mock data. It does not implement persistence, CRUD, pricing rules, PDF generation, backups, imports, file sharing, or external integrations.

## Principles

- Prefer clear, calm operational UI over marketing copy.
- Keep privacy and local-first behavior understandable to non-technical users.
- Future legal and privacy screens must work offline, be readable, selectable, and accessible.
- Future document-generation flows must make it clear that generated documents are working templates, not legal, tax, fiscal, or professional certifications.
- Future destructive actions must be explicit, reversible when possible, and confirmed when data loss is possible.

## Accessibility Baseline

- Use Material 3 components where possible.
- Preserve readable text sizes and contrast.
- Avoid icon-only controls without labels or tooltips.
- Keep legal text scrollable and selectable when implemented.

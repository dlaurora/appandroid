# Threat Model

## Scope

MVP Android app, offline-first and local-first. Implemented data includes customer details and local catalog service/product records. Future data may include quotes, amounts, technical report text, photos, exported files, and backups.

## Threat Register

| Threat | Asset affected | Actor or scenario | Impact | Likelihood | Mitigation | Status | Test or validation |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Lost or stolen device | Local Room database and internal files | Device is lost while unlocked or poorly protected | Exposure of customer and commercial data | Medium | Store in app-private storage; avoid external storage; document user responsibility for device lock and backups; evaluate local encryption before production | Open | Review storage paths and privacy copy |
| Android backup captures local app data unexpectedly | Local Room database | Auto Backup or device transfer includes persisted clients or catalog records before an approved backup/import feature | Unexpected disclosure or uncontrolled restore behavior | Medium | Exclude database domain in backup and data extraction rules during Phase 3 | Mitigated for Phase 3 | Inspect backup XML and manifest |
| Unauthorized quote or report access | Quotes, reports, PDFs | Another app or person accesses exported files | Exposure of personal and commercial data | Medium | Share only by explicit user action; use content URIs; avoid broad storage permissions | Open | FileProvider and sharing tests when implemented |
| Accidental PDF data exposure | Exported PDFs | PDF includes hidden debug data, internal paths, stack traces, or unintended content | Data leakage | Medium | Generate PDFs from sanitized models; inspect output metadata; avoid debug content | Not started | PDF validation tests when implemented |
| Sensitive data in logs | Customer and commercial data | Developer logs or release logs include user data, catalog names, SKU, or prices | Data leakage | Medium | No production logging of client or catalog fields; future central logging wrapper must redact sensitive values | Open | Static search and release build review |
| Malformed or malicious backup | Database and backup import path | User imports corrupt or crafted backup | Data corruption or denial of service | Medium | Versioned format; schema validation; preventive backup; confirmation before overwrite | Not started | Import validation tests when implemented |
| Manipulated URI or attachment | Local files and image processing | External URI points to unexpected content | Data exposure or resource exhaustion | Medium | Validate MIME, extension, size, count, and URI scheme | Not started | Attachment validation tests when implemented |
| Incorrect FileProvider scope | Internal files | Provider exposes too broad a path | Data leakage | Medium | Minimal paths XML; content URIs only; temporary read grants | Not started | Manifest/provider review when implemented |
| Vulnerable dependency | App code and data | Dependency has known vulnerability or risky data behavior | Data exposure or compromise | Low to Medium | Pin versions; maintain inventory; review changelogs before updates | Open | Dependency inventory review |
| Residual temporary files | Temporary PDFs/images/backups | Temp files remain after export/share | Unnecessary data retention | Medium | Use cache/internal temp; delete after sharing when feasible; document retention | Not started | Temp cleanup tests when implemented |
| Export/import error | User data | Failed export or import leaves partial state | Data loss or corruption | Medium | Atomic writes; validation; user confirmation; recovery backup | Not started | Export/import tests when implemented |
| Shared-link social engineering | Shared documents and contact flows | User sends documents through untrusted apps or links | User harm or privacy loss | Low to Medium | Clear user-facing copy; no automatic arbitrary links | Not started | UI review when implemented |
| Quote mistaken for fiscal/legal document | Generated documents | User treats quote as invoice, contract, certificate, or legal document | Business or legal risk | Medium | In-app disclaimer; terms of use; document footer when document generation exists | Not started | Content review when implemented |

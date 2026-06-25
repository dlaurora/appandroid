# Open Risks

| ID | Risk | Severity | Status | Owner | Mitigation | Blocking phase close |
| --- | --- | --- | --- | --- | --- | --- |
| R-001 | Local database encryption decision is not made | Medium | Open | Project owner | Evaluate SQLCipher, platform-backed encryption, or an explicit non-encryption acceptance before commercial production with real client data | No for Phase 2 development; yes before sensitive production data |
| R-002 | Legal policy drafts require professional review before publication | High | Open | Project owner | Review with qualified professional before commercial release | Yes before publication |
| R-003 | FileProvider scope is not defined because sharing is not implemented | Medium | Open | Android implementer | Define and test when export/share phase starts | No until sharing exists |
| R-004 | Google Play Data Safety draft must be validated against final dependencies | High | Open | Release owner | Review before Play submission | Yes before publication |
| R-005 | Support email, legal entity, jurisdiction, and public privacy URL are not final | High | Open | Project owner | Resolve before Play publication | Yes before publication |
| R-006 | In-app legal and privacy screens are draft-only and need professional review | High | Open | Project owner | Review and finalize content before commercial publication | Yes before publication |
| R-007 | Phase 2 has archive/restore but no permanent delete flow | Medium | Open | Project owner | Define user-facing delete/data-erasure behavior in a later approved phase | No for Phase 2 |

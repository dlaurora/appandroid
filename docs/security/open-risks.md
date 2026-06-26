# Open Risks

| ID | Risk | Severity | Status | Owner | Mitigation | Blocking phase close |
| --- | --- | --- | --- | --- | --- | --- |
| R-001 | Local database encryption decision is not made | Medium | Open | Project owner | Evaluate SQLCipher, platform-backed encryption, or an explicit non-encryption acceptance before commercial production with real client data | No for Phase 2 development; yes before sensitive production data |
| R-002 | Legal policy drafts require professional review before publication | High | Open | Project owner | Review with qualified professional before commercial release | Yes before publication |
| R-003 | FileProvider scope could expose more files than intended | Medium | Mitigated for Phase 5 | Android implementer | FileProvider exposes only `cache/quote-pdfs/`; verify manifest/XML and content URI tests before release | No if Phase 5 verification passes |
| R-004 | Google Play Data Safety draft must be validated against final dependencies | High | Open | Release owner | Review before Play submission | Yes before publication |
| R-005 | Support email, legal entity, jurisdiction, and public privacy URL are not final | High | Open | Project owner | Resolve before Play publication | Yes before publication |
| R-006 | In-app legal and privacy screens are draft-only and need professional review | High | Open | Project owner | Review and finalize content before commercial publication | Yes before publication |
| R-007 | Phase 2 has archive/restore but no permanent delete flow | Medium | Open | Project owner | Define user-facing delete/data-erasure behavior in a later approved phase | No for Phase 2 |
| R-008 | Phase 3 catalog has deactivate/restore but no permanent delete flow | Medium | Open | Project owner | Define user-facing catalog deletion/data-erasure behavior in a later approved phase | No for Phase 3 |
| R-009 | Phase 4 quotes have archive/restore but no permanent delete flow | Medium | Open | Project owner | Define user-facing quote deletion/data-erasure behavior in a later approved phase | No for Phase 4 |
| R-010 | Quote output may be mistaken for invoice, tax, legal, or certified document | High | Mitigated with residual legal risk | Project owner | Phase 5 PDFs include budget-not-invoice disclaimer; privacy/terms/legal copy still require professional review before publication | No for Phase 5 development; yes before commercial publication |
| R-011 | Shared or saved PDFs leave app control after user action | Medium | Open | Project owner | Keep sharing explicit, use `content://` temporary grants, document user responsibility, and avoid automatic public storage | No for Phase 5 development; yes before publication copy finalization |

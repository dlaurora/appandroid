# Data Classification

| Data type | Sensitivity | Storage location | Sharing rule | Retention rule |
| --- | --- | --- | --- | --- |
| Customer name | Personal data | App-private Room database | No sharing in current MVP | Archive/restore only; future delete policy required |
| Business name | Potential personal or commercial data | App-private Room database | No sharing in current MVP | Archive/restore only; future delete policy required |
| Phone and email | Personal data | App-private Room database | No sharing in current MVP | Archive/restore only; future delete policy required |
| Address | Personal data | App-private Room database | No sharing in current MVP | Archive/restore only; future delete policy required |
| Client notes | Potentially sensitive personal or commercial data | App-private Room database | No sharing in current MVP | Archive/restore only; future delete policy required |
| Service catalog name, description, category | Commercial reference data; avoid personal data | App-private Room database | No sharing in Phase 3 | Deactivate/restore only in Phase 3; future delete policy required |
| Product catalog name, description, SKU, category | Commercial reference data; avoid personal data | App-private Room database | No sharing in Phase 3 | Deactivate/restore only in Phase 3; future delete policy required |
| Catalog prices and quantities | Commercial reference data | App-private Room database | No sharing in Phase 3 | Deactivate/restore only in Phase 3; future delete policy required |
| Quote numbers, titles, descriptions, client display snapshots | Personal or commercial data | App-private Room database | No sharing in Phase 4 | Archive/restore only in Phase 4; future delete policy required |
| Quote line item names, descriptions, quantities, prices, discounts, taxes, notes, terms, amounts, and totals | Commercial data; may include personal data if entered by user | App-private Room database | No sharing in Phase 4 | Archive/restore only in Phase 4; future delete policy required |
| Technical report text | Potentially sensitive work data | App-private Room database | Share only in generated report PDFs by explicit action | Archive/restore only in Phase 6; future delete policy required |
| Report photos | Potentially sensitive media | App-private internal files after Photo Picker selection and processing | Included only in generated report PDFs by explicit action | Removed from report drafts when user removes attachment; future full delete policy required |
| PDFs | Exported documents | App-private cache before explicit export/share | Share/open via content URI only; save copy through SAF | Delete temp files when feasible after sharing |
| Backups | Full app data snapshot | Future user-selected destination | User action only | User is responsible for external backup retention |
| Legal document version shown | App metadata | Future app-private preferences | Not shared | Retain until updated or app data cleared |

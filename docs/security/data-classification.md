# Data Classification

| Data type | Sensitivity | Storage location | Sharing rule | Retention rule |
| --- | --- | --- | --- | --- |
| Customer name | Personal data | Future app-private database | Share only in user-generated documents by explicit action | Delete when user deletes customer or app data |
| Phone and email | Personal data | Future app-private database | Share only by explicit user action | Delete when user deletes customer or app data |
| Address | Personal data | Future app-private database | Share only in user-generated documents by explicit action | Delete when user deletes customer or app data |
| Quote amounts and totals | Commercial data | Future app-private database | Share only in generated documents by explicit action | Delete when user deletes quote or app data |
| Technical report text | Potentially sensitive work data | Future app-private database | Share only in generated documents by explicit action | Delete when user deletes report or app data |
| Photos | Potentially sensitive media | Future app-private internal files or user-selected URI access | Share only by explicit action | Delete temp copies when no longer needed |
| PDFs | Exported documents | Future cache/internal temp before explicit export/share | Share via content URI only | Delete temp files when feasible after sharing |
| Backups | Full app data snapshot | Future user-selected destination | User action only | User is responsible for external backup retention |
| Legal document version shown | App metadata | Future app-private preferences | Not shared | Retain until updated or app data cleared |

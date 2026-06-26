# Legal Readiness Review

## Status

Draft only. This is not legal advice.

Phase 1 added offline in-app screens for privacy policy and terms of use. Phase 2 added local client persistence. Phase 3 added catalog policy coverage. Phase 4 adds local quote policy coverage. Phase 5 adds local quote PDF generation, preview, SAF save-copy, and user-initiated FileProvider sharing/opening. Phase 6 adds local technical reports, report image attachments, and report PDFs, but commercial publication still requires professional review.

Current legal content is not approved for commercial publication.

## Required Before Publication

- Professional legal review of privacy policy.
- Professional legal review of terms of use.
- Professional legal review of service/support policy.
- Final support contact.
- Final privacy contact.
- Final responsible developer or legal entity.
- Final jurisdiction and applicable law decisions if required.
- Stable public privacy policy URL.
- Stable public terms URL if required by distribution channel or business decision.
- Stable public service/support policy URL if referenced by store listing or app.
- Alignment between in-app legal screens, repository docs, store listing, and Google Play Data Safety.

## Phase 3 Legal Gate

Catalog work must not close until privacy, terms, and service/support policy drafts accurately cover:

- local catalog storage;
- service/product names, descriptions, SKU, prices, categories, and quantities;
- no external transmission;
- no backup/import guarantee;
- no local encryption claim unless implemented;
- archive/deactivate behavior versus physical deletion;
- user responsibility for entered prices, terms, and customer/business data.

## Phase 4 Legal Gate

Quote work must not be considered publication-ready until privacy, terms, and service/support policy drafts accurately cover:

- local quote storage;
- quote numbers, client references, client display-name snapshots, line items, prices, discounts, taxes, totals, notes, and terms;
- no external transmission, PDF export, sharing, invoice generation, accounting integration, or tax/fiscal compliance claim;
- no backup/import guarantee;
- no local encryption claim unless implemented;
- archive/restore behavior versus physical deletion;
- user responsibility for validating prices, taxes, discounts, commercial terms, legal language, and local compliance before using a quote.

## Phase 5 Legal Gate

Quote PDF work must not be considered publication-ready until privacy, terms, and service/support policy drafts accurately cover:

- local PDF generation from quote data;
- temporary app-cache PDF files;
- user-selected SAF copies;
- user-initiated sharing/opening with other apps;
- no invoice, receipt, tax filing, fiscal, accounting, legal, safety, or certification compliance claim;
- the PDF disclaimer: `Este documento es un presupuesto de trabajo y no constituye una factura fiscal.`;
- user responsibility for reviewing commercial terms and compliance before sharing or saving a PDF.

## Phase 6 Legal Gate

Technical report work must not be considered publication-ready until privacy, terms, and service/support policy drafts accurately cover:

- local technical report storage;
- client references, optional approved quote references, technician names, device/asset descriptions, problem, diagnosis, work performed, recommendations, and image attachments;
- local report PDF generation, temporary app-cache PDF files, SAF save copies, and user-initiated sharing/opening;
- no legal, tax, fiscal, safety, professional certification, digital signature, or certified technical compliance claim;
- no backup/import guarantee;
- no local encryption claim unless implemented;
- archive/restore behavior versus physical deletion;
- user responsibility for reviewing report content and compliance before sharing or saving a PDF.

# Phase 5 Quote PDF Template Spec

## Document Intent

The PDF is a professional work quote for review and sharing. It is not a fiscal invoice, tax receipt, safety certification, or accounting document.

## Required Content

- Business or technician name.
- Optional business contact fields: phone, email, address.
- Quote number.
- Quote title.
- Quote status label.
- Client display name.
- Issue date.
- Valid-until date when present.
- Generated date/time.
- Item table with type, name, description, quantity, unit price, discount, and line total.
- Summary with subtotal, quote-level discount, tax line when enabled, and total.
- Notes when present.
- Terms and conditions when present.
- Page number in the footer.
- Disclaimer: `Este documento es un presupuesto de trabajo y no constituye una factura fiscal.`

## Layout

The document uses A4 portrait pages with a restrained professional layout:

- Header: business identity on the left; quote number and generated timestamp on the right.
- Client and quote metadata block below the header.
- Item table as the main body.
- Summary block after items.
- Notes and terms blocks after summary.
- Footer: app label, disclaimer reference, and page number.

## Empty And Long Content

- Empty optional business fields are omitted.
- Empty notes and terms are omitted.
- Long client names, item names, descriptions, notes, and terms wrap.
- Many items paginate.
- Table headers repeat when item rows continue on a new page.
- Monetary labels use the existing TechQuote display convention.

## Accessibility And UX Alignment

The PDF content is readable at phone preview scale and external PDF app scale. UI actions around the PDF have visible labels and state feedback; no icon-only control is introduced in Phase 5.

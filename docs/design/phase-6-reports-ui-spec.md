# Phase 6 Reports UI Spec

## Screens

- Reports list: active or archived reports, search, status filter, sort, empty/loading/error states.
- Report form: active client picker, title, service date, technician, device/asset, problem, diagnosis, work performed, recommendations, Photo Picker attachments, save draft.
- Report detail: read-only report data, gallery metadata, timestamps, state actions, duplicate, archive/restore, PDF actions.
- Report from quote: creates a draft report from an approved quote and opens the editable report form.

## Rules

- Only draft reports are editable.
- Completed and cancelled reports are read-only; duplication creates a new draft.
- Attachments can be added/removed only while the report is a draft.
- Manual reports require an active client.
- Reports from quote require an approved quote and do not copy monetary data.

## Accessibility And Empty States

All core actions use text labels. Empty states explain whether the list is active or archived. Errors avoid internal paths, filenames from external sources, full URIs, prices, or sensitive content.

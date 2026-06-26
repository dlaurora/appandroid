# Photo Picker And Attachments Review

## Picker Decision

Phase 6 uses Android Photo Picker through Activity Result APIs for report image selection. This avoids gallery, storage, and camera permissions.

## URI Handling

Picker URIs are treated as temporary read sources. The app copies selected images into private app storage immediately when possible. It does not persist external URI grants and does not store external `content://` or `file://` values in Room.

## Processing

Images are streamed to temporary app cache, validated, decoded with sampling, oriented from EXIF when available, and recompressed as JPEG. The recompressed file intentionally excludes original metadata.

Limits:

- 8 images per report
- 10 MB per input image
- 40 MB total attachment bytes per report
- 1600 px max processed edge

## Failure Behavior

Unsupported, oversized, unreadable, missing, or corrupt images produce controlled UI errors or are marked unavailable during PDF generation. Full paths and full URIs are not displayed or logged.

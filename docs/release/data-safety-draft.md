# Google Play Data Safety Draft

## Status

Draft. Must be validated against final app behavior and all dependencies before Google Play submission.

## Current Scaffold Behavior

- No accounts.
- No declared Android permissions.
- No telemetry.
- No analytics.
- No advertising SDK.
- No external network transmission implemented.
- No business data models implemented.

## Future MVP Intended Behavior

Users may enter customer, contact, quote, amount, report, note, photo, business configuration, exported PDF, and backup data. Intended storage is local on-device unless the user explicitly exports or shares.

## Third-Party Code Review

Every dependency and SDK must be reviewed for data collection and sharing behavior. The Data Safety form must reflect third-party code behavior, not only TechQuote code behavior.

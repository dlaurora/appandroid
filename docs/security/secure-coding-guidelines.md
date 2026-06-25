# Secure Coding Guidelines

- Validate all external input: intents, URIs, files, backups, imports, and user-selected content.
- Prefer typed domain models over unstructured strings when a feature introduces business concepts.
- Do not use reflection, command execution, dynamic code loading, or WebView in the MVP.
- Do not deserialize untrusted data without strict validation.
- Prefer Room queries and parametrized queries; avoid raw SQL unless justified.
- Handle specific exceptions and show user-safe errors.
- Do not show stack traces or internal paths to users.
- Do not use homemade cryptography.
- Review every change that touches backup, import, PDF, attachments, permissions, storage, dependencies, logs, release config, or sharing.
- Keep generated files and exported documents free of debug data, internal paths, stack traces, and hidden sensitive metadata.

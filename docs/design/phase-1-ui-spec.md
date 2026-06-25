# Phase 1 UI Spec

## Scope

Phase 1 builds the visual shell for TechQuote. It includes a design system, reusable Compose components, Navigation Compose routes, mock-only placeholder screens, and Compose previews.

Phase 1 does not implement business logic, persistence, real clients, real catalog items, real quotes, real reports, PDFs, backups, imports, file sharing, permissions, external integrations, analytics, telemetry, or real contact actions.

## Visual Direction

TechQuote uses a calm, professional, technical interface for small business operations. The palette is sober and readable, with blue as the main action color, teal as the supporting color, neutral surfaces, and semantic colors for status and validation.

The UI should feel practical instead of promotional. Screens prioritize clear titles, dense but readable lists, visible primary actions, and offline/local-first reassurance where relevant.

## Theme Modes

The design system supports:

- Light theme.
- Dark theme.
- Follow-system behavior through `isSystemInDarkTheme()`.

The app defaults to follow system. Phase 1 settings are visual only and do not persist a selected theme.

## Tokens

Tokens are centralized under `com.techquote.app.ui.theme`.

| Token group | Responsibility |
| --- | --- |
| Color scheme | Material 3 light and dark color schemes |
| Semantic colors | Draft, sent, approved, rejected, error, warning, and success states |
| Typography | App-level Material 3 typography scale |
| Spacing | Screen padding, component gaps, touch target minimums |
| Shapes | Card, input, chip, and dialog corner shapes |
| Elevation | Card, raised, and overlay elevation values |

Screens should use tokens and reusable components instead of hardcoded colors, typography, spacing, or shape values when a token exists.

## Semantic States

Status chips and supporting UI must not communicate status by color alone. Labels are always visible.

| State | Use |
| --- | --- |
| Draft | Simulated editable or not-yet-sent records |
| Sent | Simulated sent quote/report status |
| Approved | Simulated positive business status |
| Rejected | Simulated negative business status |
| Error | UI error and destructive feedback |
| Warning | Caution and incomplete simulated setup |
| Success | Successful simulated action feedback |

## Components

Phase 1 introduces these reusable Compose components:

- `AppTopBar`
- `TechQuoteScaffold`
- `PrimaryButton`
- `SecondaryButton`
- `DangerButton`
- `EmptyState`
- `LoadingState`
- `ErrorState`
- `SearchField`
- `FormTextField`
- `CurrencyTextField`
- `DateField`
- `StatusChip`
- `ConfirmDeleteDialog`
- `SectionHeader`
- `QuickActionCard`
- `ListItemCard`

Components are visual and callback-driven. They do not write data, access files, request permissions, call external apps, or reach persistence.

## Screen State Pattern

Each relevant screen accepts a mock UI state and callbacks:

- Content.
- Empty.
- Loading.
- Error.

Screens render state-specific UI through shared state components. Routes may show simulated feedback with a snackbar. Destructive actions are simulated and must show a confirmation dialog before showing feedback.

## Preview Pattern

Each important screen and component has a light and dark Compose Preview through a grouped preview annotation. Previews use pure deterministic mock data and do not depend on Room, Hilt ViewModels, files, permissions, network, PDFs, or external APIs.

## Accessibility

Phase 1 UI must:

- Use scalable text from Material 3 typography.
- Preserve sufficient contrast in light and dark themes.
- Keep touch targets at least 48 dp where a token exists.
- Avoid icon-only interactive controls.
- Provide readable labels for state, actions, and navigation.
- Avoid color-only state indication.
- Remain usable on small phone widths and in landscape through scrollable layouts.

## Security And Privacy

All mock data is fictitious, deterministic, and safe. It must not contain real names, companies, addresses, phone numbers, emails, identifiers, prices tied to real clients, or real legal facts.

Phase 1 keeps the app permission-free and offline. It does not add logging, telemetry, storage, sharing, FileProvider paths, WebView, or external processors.

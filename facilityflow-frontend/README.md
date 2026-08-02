# FacilityFlow — Frontend

Angular 19 (standalone, signals) frontend for the FacilityFlow enterprise facility & asset management platform. Dark slate glassmorphism design system, consistent with the FlowSync/ShiftSync visual language.

---

## 1. Stack

- **Angular 19** — standalone components, signals for state, `@if`/`@for`/`@switch` control flow
- **Reactive Forms** for all create/edit flows
- **SCSS** with a CSS-custom-property design token system (`src/styles/_tokens.scss`)
- **Functional HTTP interceptors** for JWT attachment + silent refresh-token rotation
- No NgModules anywhere — every component is `standalone: true` and lazy-loaded via `loadComponent`

---

## 2. Getting Started

### Prerequisites
- The FacilityFlow backend running at `http://localhost:8080` (via `docker compose up` in that project)
- Node.js 20+ and npm

### Install & run

```bash
npm install
npm start
```

The app serves at `http://localhost:4200` and calls the API at `http://localhost:8080/api/v1` (see `src/environments/environment.ts`).

Log in with any of the seeded backend accounts (password `Password123`):

| Email | Role |
|---|---|
| admin@facilityflow.com | ADMIN |
| manager@facilityflow.com | FACILITY_MANAGER |
| employee@facilityflow.com | EMPLOYEE |

### Build

```bash
npm run build                              # development build, output in dist/
npx ng build --configuration production    # production build
```

> **Note on production builds:** Angular's production build tries to inline Google Fonts by fetching `fonts.googleapis.com` at build time. This is disabled in `angular.json` (`optimization.fonts: false`) so the build works in network-restricted CI/sandbox environments — fonts still load normally at runtime via the `<link>` tags in `index.html`. If your build environment has full internet access and you'd like build-time font inlining (marginally faster first paint), you can re-enable it.

---

## 3. Project Structure

```
src/app/
├── core/
│   ├── models/          TypeScript interfaces mirroring backend DTOs
│   ├── services/        One service per API resource (auth, users, tickets, ...)
│   ├── interceptors/    JWT attachment + silent refresh, global error toasts
│   └── guards/          authGuard, guestGuard, roleGuard(['ADMIN'])
├── layout/
│   └── shell/            Sidebar + topbar shell wrapping all authenticated routes
├── features/
│   ├── auth/              Login, register
│   ├── dashboard/         Stats, priority/status bar charts, top buildings
│   ├── facilities/
│   │   ├── buildings/     Building CRUD
│   │   └── rooms/         Room CRUD, cascading building→floor select
│   ├── assets/            Asset CRUD, QR code display
│   ├── tickets/           List + detail (assignment, status workflow, comments)
│   ├── reservations/      Booking, approve/reject, my vs. all view
│   ├── notifications/     Notification inbox
│   ├── users/             Admin: role changes, enable/disable, delete
│   ├── audit-logs/        Admin: filterable audit trail
│   └── profile/           Current user's profile + password change
└── shared/components/     Reusable UI: modal, badge, stat-card, page-header,
                           empty-state, pagination, toast-host
```

---

## 4. Design System

All visual tokens live in `src/styles/_tokens.scss` as CSS custom properties — colors, spacing, radius, shadows, motion. No hardcoded hex values in component styles; everything references a `--ff-*` variable so the whole app re-themes from one file.

- **Palette**: near-black slate background, blue accent (`--ff-accent`), teal secondary, semantic success/warning/danger/info tones
- **Surfaces**: `glass-panel` mixin (blur + subtle gradient + border) used for every card/modal/panel
- **Typography**: Sora for headings, Inter for body, JetBrains Mono for codes/tags/timestamps
- **Status badges**: a single `<ff-badge>` component maps every backend enum (room status, asset status, ticket priority/status, reservation status, roles, audit actions) to a consistent color tone

---

## 5. State & Data Flow

- **Auth state** lives in `AuthService` as signals (`currentUser`, `isAuthenticated`, `role`, `isAdmin`, `isManagerOrAdmin`) — components read these directly, no manual subscriptions needed.
- **Token refresh** is handled transparently in `authInterceptor`: on a 401 from an authenticated request, it attempts one silent refresh via the stored refresh token before giving up and logging out.
- **Toasts** are a simple signal-backed queue (`ToastService`) rendered by `<ff-toast-host>` in the app root — any service or component can call `toast.success(...)` / `toast.error(...)` without wiring up subscriptions.
- **Errors** are caught globally in `errorInterceptor` and surfaced as toasts, so feature components don't need repetitive `error:` handlers for the common case.

---

## 6. Known Simplifications

Given the scope, a few things were kept intentionally simple and would be worth revisiting for a real production rollout:

- Delete confirmations use the native `confirm()` dialog rather than a custom modal.
- The reservation "calendar view" endpoint exists on the backend but isn't yet wired into a visual calendar UI (reservations are shown as a list, filterable by "mine" vs "all").
- Floor management has a backend service and API wired up (`FacilityService.listFloors/createFloor`) but no dedicated UI screen — floors are currently only selected (not created) from the Rooms page's cascading dropdown.
- No route-level pre-fetching/resolvers — each page fetches its own data in `ngOnInit`.

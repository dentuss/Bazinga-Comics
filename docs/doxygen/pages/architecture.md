@page architecture_overview Architecture and Data Flow

This page describes how the backend and frontend collaborate to deliver the Bazinga Comics
experience.

## Request flow

1. The frontend calls the API helper (`apiFetch`) with a path such as `/api/comics`.
2. The backend controller maps the request, applies authorization rules, and delegates to
   repositories or services.
3. Entities such as `Comic` map to relational tables and are persisted through Spring Data
   repositories.
4. Responses are serialized to JSON and rendered by React components.

## Core backend modules

- **Controllers**: Route HTTP requests and map to DTOs (`controller/`).
- **Services**: Encapsulate business logic and background tasks (`service/`).
- **Repositories**: Spring Data interfaces for persistence (`repository/`).
- **Entities**: JPA models representing domain data (`entity/`).
- **Configuration**: Security and JWT configuration (`config/`).

## Core frontend modules

- **Components**: Presentational UI building blocks (`src/components/`).
- **Pages & views**: Screen-level compositions (`src/pages/` if present).
- **API utilities**: Typed API helper in `src/lib/api.ts`.
- **Styling**: Tailwind + global styles in `src/index.css`.

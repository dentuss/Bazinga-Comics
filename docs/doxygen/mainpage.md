# Bazinga Comics Documentation {#mainpage}

@tableofcontents

Welcome to the Bazinga Comics documentation site. This Doxygen set covers both the Spring Boot
backend (APIs, security, data layer) and the Vite/React frontend (UI components, client API
helpers, styling). Each section below links to focused pages with code examples pulled directly
from the source tree.

- @ref backend_overview
- @ref frontend_overview
- @ref architecture_overview
- @ref docs_building

## Repository layout

- `bazingaBE/` — Spring Boot backend service (REST APIs, security, persistence)
- `bazingaFE/` — Vite/React frontend application (components, API utilities, styling)
- `docs/doxygen/` — Documentation configuration and authored pages

## Getting started

To generate the HTML documentation site locally, run:

```
doxygen docs/doxygen/Doxyfile
```

The generated site will be available under `docs/doxygen/site/html/index.html`.

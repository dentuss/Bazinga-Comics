@page docs_building Building the Documentation Site

## Prerequisites

- Install Doxygen (and Graphviz for diagrams if desired).
- Run commands from the repository root.

## Generate HTML

```
doxygen docs/doxygen/Doxyfile
```

Open the generated site at:

```
docs/doxygen/site/html/index.html
```

## Tips

- Add or update markdown pages under `docs/doxygen/pages/` for curated content.
- Doxygen will scan both `bazingaBE` and `bazingaFE` source trees for code reference pages.

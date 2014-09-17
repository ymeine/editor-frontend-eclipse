Root of the Eclipse plugin code.





# File system layout

- [`readme.md`](./readme.md): the main documentation file

Modules:

- [`backend/`](./backend/): interface to the backend
- [`document/`](./document/): to handle a document, that is what represents a whole source code (somehow analogous to what is usually inside a file)
- [`editor/`](./editor/): editor __view of a document__
- [`outline/`](./outline/): outline __view of a document__
- [`modes/`](./modes/): __syntax highlighting__ for specific languages
- [`data/`](./data/): for global data management for the plugin
- [`plugin/`](./plugin/): code purely related to Eclipse plugins

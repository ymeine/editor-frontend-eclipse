Activator





# File system layout

- [`Activator.java`](./Activator.java): the main implementation file
- [`readme.md`](./readme.md): the main documentation file





# Documentation

__The activator is used for the plugin lifecycle management.__

It is basically called on startup, where it can do some setup, and on shutdown where it can finally do some tear down job.

Concretely:

- __on start__: it uses the backend module to ensure an actual backend will be available
- __on stop__: it asks the backend module to stop activity

Editor





# File system layout

- [`Editor.java`](./Editor.java): the main implementation file
- [`readme.md`](./readme.md): the main documentation file





# Documentation

__The editor is a kind of a hub class, it centralizes the services related to edition, but delegates a lot to other classes.__

The main work of configuration, setup of services like highlighting and so on is delegated to a source viewer configurator, which configures the source viewer embedded in the editor.

Services more _external_ are implemented through _adapters_. A generic method named `getAdapter` is called multiple times by the editor itself. This method receives a type specification (`Class`) corresponding to the required service. It's free to the user to return or not an implementation as a response.

Currently used adapters:

- outline view

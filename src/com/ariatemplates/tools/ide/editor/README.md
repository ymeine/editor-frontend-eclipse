Editor view of a document.





# File system layout

- [`readme.md`](./readme.md): the main documentation file

Modules: 

- [`editor/`](./editor/): the editor itself
- [`configuration/`](./configuration/): handles configuration of the editor
- [`annotations/`](./annotations/): handles annotations added into the editor view
- [`highlighting/`](./highlighting/): all things related to syntax highlighting of the content of the editor





# Documentation

## Folding

Folding is currently a work in progress, and as for highlighting (at the time of writing), it works statically (doesn't support dynamic updates).

Folding is implemented in Eclipse using a _complex_ system. It has a concept of views on data.

Here, the graphical view, that the user sees, uses a model which is a view on the actual document behind. They rather call that a projection. This projection takes into account folded parts: some lines are removed, and that's why they are not displayed. However the line numbers are kept consistent, and some additional GUI components are added: that is handled by a specific text viewer.

To sum it up, folding requires both:

- having a projection document coming above the actual document: this is a document with some lines removed but with some folding information
- using a specific viewer, able to use this information

In fact, folding handles for instance use a generic feature of the text editors: annotations.





# Backlog

## Editor, Source viewer configuration or document module?

Move some services from the editor to the source viewer configuration class or the document module.

## Editor configuration at initialization

Fix the mode resolution in the source viewer configuration (refer to the class itself for more information).

## Other services

__Implement other services.__

There are two parts in implementing a service:

- communicating with the backend to get required data for the service
- using this data and apply specific processing with it on the client to _complete_ the service

Services to be set up:

1. Formatting
1. Content assist

## Documentation

### Folding

__Complete the documentation the work that has been done for folding.__

- review what has been written already
- put the references of the resources that were used (mainly copied and arranged code from [this tutorial](http://www.eclipse.org/articles/Article-Folding-in-Eclipse-Text-Editors/folding.html))

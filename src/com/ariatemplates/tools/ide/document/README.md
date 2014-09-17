Manages a document, a document being the model used to handle data that can be edited through editors.





# File system layout

- [`readme.md`](./readme.md): the main documentation file

Modules: 

- [`document/`](./document/): a custom document implementation, considering the specificities the backend introduces with the document model
- [`listener/`](./listener/): a module used to intercept and handle changes events in a document
- [`partitioner/`](./partitioner/): a generic simple document partitioner
- [`provider/`](./provider/): a module that returns a document instance given a document input

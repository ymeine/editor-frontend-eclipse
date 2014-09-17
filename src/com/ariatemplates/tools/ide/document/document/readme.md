Document





# File system layout

- [`Document.java`](./Document.java): the main implementation file
- [`readme.md`](./readme.md): the main documentation file





# Documentation

__Extends a standard document implementation.__

The things this class adds compared to the standard document implementation it inherits from is related to the way a document is handled by the backend.

For now, the essential addition is the storage of the id of the document, returned by the backend when it has been registered. This is the only required data to be able to process a document on the backend.

__Maybe add some caching features.__ The plugin will sometimes want to get some information stored in the backend about a document. This information, if known to likely remain unchanged, could be cached in this class.

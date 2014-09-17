Document partitioner





# File system layout

- [`Partitioner.java`](./Partitioner.java): the main implementation file
- [`readme.md`](./readme.md): the main documentation file





# Documentation

__Sets up _big_ partitions of a document.__

__For now the document always returns a unique partition for the whole document with the generic name:__ `MAIN`.

This is more likely to be used in order to handle multiple languages in a same document.

However, the backend implementation already handles this, so here there would be only one partition per document, corresponding to the _master_ language of the file.

Manages a document, a document being the model used to handle data that can be edited through editors.

# File system layout

- [`readme.md`](./readme.md): the main documentation file
* [`POCDocument.java`](./POCDocument.java): a custom document implementation, considering the specificities the backend introduces with the document model
* [`POCDocumentListener.java`](./POCDocumentListener.java): a class used to intercept and handle changes in a document
* [`POCDocumentPartitioner.java`](./POCDocumentPartitioner.java): a generic simple partitioner
* [`POCDocumentProvider.java`](./POCDocumentProvider.java): a class that is able to return a document instance given a document input

# Versioning

To version: _everything_.

# Documentation

## Document

__Extends a standard document implementation.__

The things this class adds compared to the standard document implementation it inherits from is related to the way a document is handled by the backend.

For now, the essential addition is the storage of the id of the document, returned by the backend when it has been registered. This is the only required data to be able to process a document on the backend.

__Maybe add some caching features.__ The plugin will sometimes want to get some information stored in the backend about a document. This information, if known to likely remain unchanged, could be cached in this class.

## Document provider

__Is in charge to create a document instance given a document input.__

For the time being, we use file documents input ( ___it might change in the future, with the concept of sessions, sharing - that is editing a same document through multiple frontends - , ...___ ), so we subclassed the `FileDocumentProvider` class, and we use the base implementation to actually create the document.

Doing this way just enables us to hook up the document creation process, so we can apply configuration on the requested document.

For now the document partitioner is applied that way, but this could also be done with a [document setup participant class](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fcore%2Ffilebuffers%2FIDocumentSetupParticipant.html).

## Document partitioner

__Sets up _big_ partitions of a document.__

__For now the document always returns a unique partition for the whole document with the generic name:__ `MAIN`.

This is more likely to be used in order to handle multiple languages in a same document.

However, the backend implementation already handles this, so here there would be only one partition per document, corresponding to the _master_ language of the file.


# Contribute

## Update

__Improve document updates.__

When the user modifies the content of the document, send the changes to the backend.

There are two problematics linked to this subject:

* the content: what should be sent for a document change: a diff instad of the whole document is the best solution (with actions like _inserted_, _replaced_, _removed_, _moved_, ...)
* the performance/timing: when should the changes be sent

### Frequency/timing

The frequency of the update is important for both performances, user experience, and also content.

The unit of a change can be a keystroke: entering a character or removing. Remember that changes can occur fast: with one keystroke on a selection a whole document can be erased for instance.

So we could think that we should update on each keystroke. However, we have to consider the user too: he can type in very fast. And though this doesn't necessarily mean introducing big changes! Imagine typing 5 characters and removing them with backspace in one second: nothing changed, but you would have made 10 updates in 1 second! And I didn't even talked about undo/redo commands...

### Conclusion

Without any further details (the reflection on this subject could go far), here is an idea of solution: on the client side, changes should be concatenated for some amount of time, and then updates sent at the corresponding frequency.

Hopefully the used document implementation already concatenates quick changes and applies squashing algorithms.

Example: send updates every 250ms, time during which the client should do itself a concatenation of changes.

## Document registering & mode detection

__Make the document provider infer the mode of the document at registration.__

The registration of a document requires a mode (language) to be set for the document.

To know the mode of a document, there can be two solution:

* the simple one: look at the filename, and particularly the extension (or a special file name)
* the complex one: analysing the content of the document (makes sense if not empty) to try to guess the mode

The second solution involves semantics and should be handled by the backend. For now it doesn't support this feature, so we should take the first one.

__Note: even the mode inference through filename should be handled by the backend. And this should be configurable by the client (extensions/modes mapping for instance)__

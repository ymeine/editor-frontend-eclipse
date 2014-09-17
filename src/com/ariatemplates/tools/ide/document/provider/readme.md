Document provider





# File system layout

- [`Provider.java`](./Provider.java): the main implementation file
- [`readme.md`](./readme.md): the main documentation file





# Documentation

__Is in charge to create a document instance given a document input.__

For the time being, we use file documents input ( ___it might change in the future, with the concept of sessions, sharing - that is editing a same document through multiple frontends - , ...___ ), so we subclassed the `FileDocumentProvider` class, and we use the base implementation to actually create the document.

Doing this way just enables us to hook up the document creation process, so we can apply configuration on the requested document.

For now the document partitioner is applied that way, but this could also be done with a [document setup participant class](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fcore%2Ffilebuffers%2FIDocumentSetupParticipant.html).





# Backlog

## Document registering & mode detection

__Infer the mode of the document at registration.__

The registration of a document requires a mode (language) to be set for the document.

To know the mode of a document, there can be two solution:

- the simple one: look at the filename, and particularly the extension (or a special file name)
- the complex one: analyzing the content of the document (makes sense if not empty) to try to guess the mode

The second solution involves semantics and should be handled by the backend. For now it doesn't support this feature, so we should take the first one.

__Note: even the mode inference through filename should be handled by the backend. And this should be configurable by the client (extensions/modes mapping for instance)__

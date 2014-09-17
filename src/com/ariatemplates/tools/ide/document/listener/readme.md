Document listener





# File system layout

- [`Listener.java`](./Listener.java): the main implementation file
- [`readme.md`](./readme.md): the main documentation file





# Backlog

## Improve document updates

When the user modifies the content of the document, send the changes to the backend.

There are two problematics linked to this subject:

- the content: what should be sent for a document change: a _diff_ instead of the whole document is the best solution (with actions like _inserted_, _replaced_, _removed_, _moved_, ...)
- the performance/timing: when should the changes be sent

### Frequency/timing

The frequency of the update is important for both performances, user experience, and also content.

The unit of a change can be a keystroke: entering a character or removing. Remember that changes can occur fast: with one keystroke on a selection a whole document can be erased for instance.

So we could think that we should update on each keystroke. However, we have to consider the user too: he can type in very fast. And though this doesn't necessarily mean introducing big changes! Imagine typing 5 characters and removing them with backspace in one second: nothing changed, but you would have made 10 updates in 1 second! And I didn't even talked about undo/redo commands...

### Conclusion

Without any further details (the reflection on this subject could go far), here is an idea of solution: on the client side, changes should be concatenated for some amount of time, and then updates sent at the corresponding frequency.

Hopefully the used document implementation already concatenates quick changes and applies squashing algorithms.

Example: send updates every 250ms, time during which the client should do itself a concatenation of changes.

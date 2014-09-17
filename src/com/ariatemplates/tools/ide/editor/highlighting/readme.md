Highlighting




# File system layout

- [`readme.md`](./readme.md): the main documentation file

Modules: 

- [`tokens/`](./tokens/): tokens delimit semantic portions of code





# Backlog

## Finish the first implementation of highlighting

Highlighting is a huge topic, because Eclipse RCP tries to provide a lot of support with efficiency in mind.

It involves things like:

- damager/repairer protocol
- token scanner

The second one, __the token scanner__, is explained [here](./tokens/scanner/) and is something that must be able to return tokens for some range of code.

It is rather simple, because it takes as input a range that it must store, and then is queried for tokens inside this range. The only _hard_ thing to handle is returning the set of tokens for a given range, but the backend takes care of sending the proper list of tokens.

The first thing, the __damager/repairer__ is something more complicated that has to be investigated.

It basically tries to identify which part of a document has been impacted by a change, and then asks some services to repair this part.

An example will be simple. Let's take a piece of JavaScript code that is going to be commented:

```javascript
function foo() {
	var bar = baz;
}
```

With only one insertion at one position, the highlighting changes from this position to the end of the line:

```javascript
function foo() {
	//var bar = baz;
}
```

The damager will detect that the full line has to be repaired, and then the token scanner will be invoked with the corresponding range.

__However, we need to take care that the proper range is always detected.__ For instance: with an opening multi-line comment, the whole rest of the document (until the end) must be re-highlighted.

__It would be good if for a first implementation the whole document was re-highlighted everytime.__

### Synchronization with updates

__Another important issue is to check that highlighting update is made after the document has been updated in the backend.__

Indeed, document updates are handled by the POCDocument class, and synchronization with the backend by the POCDocumentListener one.

So when the document is changed due to an input of the user, the latter will send the change to the backend, and then it's up to the client to call all necessary services to update the views.

However, we don't know when the repairer is trying to update the highlighting after a document change: before or after the document listener is called? And will the backend have the time to update it too? (this is an issue of the backend to synchronize properly the requests)

### Handle more text presentation data

The backend returns text presentation data in the stylesheet. There is information about the color of the text but also its _style_: italic, bold, etc.

For now, only the color is taken into account, so process the other information.

The method `POCTokenScanner.getAttribute()` returns the [`TextAttribute`](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjface%2Ftext%2FTextAttribute.html) instance which should take more data: use the third constructor with color, background, style and font.

Use the [`Font`](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fgraphics%2FFont.html) class to handle: bold, italic and family.

Be careful of properly handling default style (the stylesheet provides default attributes for styles skipping some of them).

## Performances

1. Cache styles
1. Cache stylesheet: for one given document or maybe for a given mode (and the mode should be retrieved from a document)
1. Pre-process entirely the stylesheet: it changes rarely (if it's not never) so it won't have to be done too often

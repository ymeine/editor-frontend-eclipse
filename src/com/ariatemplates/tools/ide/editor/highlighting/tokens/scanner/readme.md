Scanner





# File system layout

- [`Scanner.java`](./Scanner.java): the main implementation file
- [`readme.md`](./readme.md): the main documentation file





# Documentation

As for almost everything in Eclipse, you can implement custom behavior if you achieve to find the proper interface. Here it is.

The token scanner basically receives a document and a range, and must be able then to return corresponding tokens until it receives a new _update request_.

For the time being, the token scanner is used only for highlighting, so it will return tokens with styling data.

__Be careful!__

This is not obvious, but the process for highlighting expects the token scanner to return tokens __for the whole input__, I mean that if you _concatenate_ every range of each token, you must get a continuous range equaling the whole input. In clearer words: every whitespace must be taken into account.

This is not obvious since the interface of the token scanner is able to return both an offset (index) and a length for a token, so that the client can know the properties `start`, `length`, and `end` and can do everything with that.

This is even how highlithing works, it asks for each encountered token the offset and the length, and applies styling on the corresponding range, using the data embedded in the token (text presentation data).

However, this is not consistent since it tries to optimize it when consecutive tokens return the same text presentation data: in this case it just adds the lengths of the tokens, and uses the offset of the first token, in order to apply the text presentation only once.

It will be easier to understand with an example. Imagine this source code:

```javascript
a() ;
```

and the correponding tokens:

- `identifier, 0, 1`: `black`
- `punctuator, 1, 2`: `blue`
- `punctuator, 2, 3`: `blue`
- `punctuator, 4, 5`: `blue`

Eclipse will first apply the `black` style for range `0, 1`. Then, it will _concatenate_ the ranges for the three punctuators which have the same style, `blue`. In the end, it applies the style `blue` on range `1, 1+(1+1+1)` = `1, 4`, which is wrong.

To avoid this, ensure that you have a token for every portion of the source code, like this:

- `identifier, 0, 1`: `black`
- `punctuator, 1, 2`: `blue`
- `punctuator, 2, 3`: `blue`
- `whitespace, 3, 4`: `...`
- `punctuator, 4, 5`: `blue`

Have a look at the code of the method `createPresentation` of the `org.eclipse.jface.text.rules.DefaultDamagerRepairer` class for more information.

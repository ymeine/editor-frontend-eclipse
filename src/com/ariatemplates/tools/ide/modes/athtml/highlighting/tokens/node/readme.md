Node





# File system layout

- [`readme.md`](./readme.md): the main documentation file
- [`Node.java`](./Node.java): the main implementation file





# Description

An enhanced token which implements the model of a __node__ in a __graph__. Apart from the inherent structural traits, the token contains all the properties related to its semantic: description of portions of source code.

Therefore, there is no need to have multiple classes for tokens: this one is generic enough to address both structural concerns and business value, that is what a token is used for outside. This perfectly matches our model of graph used to describe source code, with required specificities to handle highlighting.




# API

## Inheritance

Inherits class [`org.eclipse.jface.text.rules.Token`](http://help.eclipse.org/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjface%2Ftext%2Frules%2FToken.html)



## Class properties

- `UNDEFINED`, singleton of self class: represents an undefined token, by setting its property `defined` to `false`. This token is used only for that, and responds `true` to overridden method `isUndefined`.
- `UNDEFINED_INT`, `int`: ??? value: `-1`



## Instance properties

__The following properties are specific to this class, don't forget to consider also its base class.__

Unless specified otherwise, properties follow the Java [POJO's JavaBeans](https://en.wikipedia.org/wiki/Plain_Old_Java_Object#JavaBeans) convention for [mutation](https://en.wikipedia.org/wiki/Mutator_method).

Basic properties: 

- `offset`, `int`: position of the token inside the whole document
- `length`, `int`: length of the token (number of characters it spans)
- `type`, `String`: the type of the token

Structural properties: 

- `children`, `List` of `IToken`: the list of child tokens

Other properties: 

- `defined`, `boolean`: this property is very specific, and should always be `true` __except__ for a unique singleton token to be used to represent the _UNDEFINED_ token (please see description of [`org.eclipse.jface.text.rules.RuleBasedScanner`](help.eclipse.org/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjface%2Ftext%2Frules%2FRuleBasedScanner.html) on how this is used)



## Instance methods

### Clone the token

Name: `clone`.

It creates a new token of self class and copies all the properties described above from the source to the new token.

It also copies the following inherited properties: 

- `data` (`getData`, `setData`): the data associated to this token. We don't have to care about what this actual is.

### Mutate children list

In addition to standard getter and setters, the following convenient methods are available: 

- `addChild`: adds a child to its `children` list
- `popChild`: removes the last child from its `children` list
- `hasChildren`: tells whether its `children` list is not empty or is

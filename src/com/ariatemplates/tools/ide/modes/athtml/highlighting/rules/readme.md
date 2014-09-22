Rules





# File system layout

- [`readme.md`](./readme.md): the main documentation file

Rules: 

- Generic: 
	- [`Container.java`](./Container.java): `Container`, the base rule
	- [`Word.java`](./Word.java): `Word`, to parse an element which has a simple set of alternatives
- Basic: 
	- Literals: 
		- [`StringRule.java`](./StringRule.java): `StringRule`
		- [`Function.java`](./Function.java): `Function`
		- [`Array.java`](./Array.java): `Array`
		- Object: 
			- [`Object.java`](./Object.java): `Object`
			- [`Key.java`](./Key.java): `Key`
- Specific: 
	- [`Statement.java`](./Statement.java): `Statement`
	- [`Expression.java`](./Expression.java): `Expression`
	- HTML elements: 
		- [`Tag.java`](./Tag.java): `Tag`
		- [`Attribute.java`](./Attribute.java): `Attribute`





# Documentation

__The method__ used to parse the document's content and find the tokens __is quite free__.

For reminder, __a scanner parses a portion of the document content__. In our case, we use __a scanner__ which is configured with __a sequence of rules__, and __tries them in order__ until one works and return a specific token, or fallbacks to a default token.

This module implements the rules.

## How does a rule work?

As said, this is quite free. A rule receives only the scanner which is using it. __From this scanner__, basic operations can be done, such as __reading__ content from the scanned portion and move the _cursor_ on it (in fact reading moves it forward while ___unreading___ rewinds it).

If the rule succeeds, __it will__ only __return a token__, that is a kind of informative data about the content it parsed. But __no information of position or length of this content is explicitly returned__: the scanner will detect it by examining its cursor. That's why it's important to keep in mind that reading moves the cursor forward, even for simple lookup. To _hide_ the lookup, or revert the cursor in case the parsing fails, _unreading_ is necessary.

__Having a good cursor position is necessary for the scanning to perform correctly__: what has been scanned must not be scanned again.

Another important thing about the fact that _standard_ tokens don't have a notion of __position (offset) and length__ is that this information __must be kept somewhere__, so that the scanner can return it. That's why we store it in our special token, please refer to its documentation.

## How to organize rules?

Once again, everything is free. There could be only one big unique rule provided to the scanner, and it would be able to return all the different tokens. But we would obviously lose all the benefits of using a rule based scanner.

What we do is that we consider a __hierarchy or rules__: __this matches the model of tokens which is a tree__.

The base and abstract rule is the `Container`. It provides all conveniences, default behaviors, shortcuts, etc. to make other rules as simple as possible. Therefore all the other rules inherit from it.

## What is the common technique for parsing?

The thing is simple: 

- read some characters until you can make the decision: __is the content matching the rule or not__?
- if no, return an _undefined_ token (this is a token which returns `true` to `isUndefined()`)
- otherwise, __continue parsing__ and return various tokens, __until you reach something that indicates the end of the rule__

There are then various ways to continue parsing and return tokens.

### Continuing parsing

The parsing continues if two conditions are met: 

- we didn't reach the end of the file (`EOF`)
- the rule is still valid

An simple technique is to maintain a boolean to know whether the rule is still valid or not. Inside the reading loop, this boolean would be altered by various checks, maybe including lookups forward or backward.

Here are however some rules to respect for this work fine: 

- only actually read one character per iteration, doing `unread` if forward lookups were necessary; note that backward lookups use an internally maintained buffer (indeed this is a much simpler technique)

### Returning tokens

Normally, a token is one block of text, but what we do a lot is to return every single component of tokens as being this token. This is not clear, so here is an example.

Imagine the word `hello`. It can be one token, from 0 to 5, with a token type `word`. But in our specific case, it also works to say that there are 5 tokens, respectively between 0-1, 1-2, 2-3, 3-4, and 4-5, all of type `word`. Actually, we don't care about saying what is a token or not, all we care about is saying __of what type is every single component__. In this case, this method works.

However, in some cases, and this is even more advised, we return full actual tokens.

### Using sub-rules

__Often, rules are containers__, which have __delimiters__ and an __undefined number of__ quite unknown __items__ inside their content. The only thing that is known are the __possible alternatives for this items__. Thats is the __hierarchy of rules__ we talked about.

When doing its parsing, a rule will therefore have to parse its sub-content. It works as for any parsing: a rule based scanner is created, on the proper portion of the document, with the set of alternative rules.

Then, __the rule asks its sub-scanner to try to find an item__. __If it doesn't work__, it means the parsed content is part of the current rule, it can be an item separator, or _whatever_, a kind of __fallback__. But __if it works__, we simply __get the token__ corresponding to the item it found __and forward it__.

Since using a sub-scanner is sort of a hack, we need to __take care to update__ the current rule's corresponding scanner's state. As said, there's not much to care about, only __the cursor position__. So we must make it read (just to move the cursor, the content has already been handled by the sub-scanner) as long as the sub-scanner has read.
 
 



# Backlog

## Centralize some code

Some code is repeated across rules, applying the techniques described here. However, there could be a base implementation, or helpers, to remove this duplication, with all the benefits it brings (simplicity, maintainability).

One of the thing I'm thinking about is to make a generic implementation of actual container rules (btw I would maybe rename the current `Container` class). This would:

- handle the first step of detection
- handle the step of parsing in the loop, using sub-rules
- manage properly default tokens and returning undefined tokens
- take care of the EOF throughout the whole process

__Procedure__: refactor all classes to extract from the main method the different steps described above. If the `evaluate` methods becomes the same for all classes, it's a win, just move it to teh base class!

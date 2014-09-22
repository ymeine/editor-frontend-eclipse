Tokens





# File system layout

- [`readme.md`](./readme.md): the main documentation file
- [`TokensStore.java`](./TokensStore.java): a factory used to create token

Tokens: 

- [`node/`](./node/): a generic token, acting as a node in the graph model (AST)





# Documentation: `TokenStore`

To be used as a __singleton__.

## Description

The store acts as a factory for tokens. It will always return an a new instance of a `Node` token, but configured with some data.

This data can be both provided by the user and inferred from the previous. For instance, if a token of a certain type is requested, data associated to this type, such as the display information (used for highlighting) will be added as well.

## List of known token types

- `default`: the type which means _no specific token_
- `container`: the type for container tokens. Remember that a token is like a node in a graph, and that we actually care only about leaves (which is the sequence of _actual_ tokens). Therefore, this type has no associated display data.
- Basic: 
	- Syntax: 
		- `comment`
		- `operator`
	- Literals: 
		- `string`
		- `number`
		- `boolean`
		- `function`
		- `array`
		- Object: 
			- `object`
			- `key`
- Specific: 
	- Statements: 
		- `statement`
		- `statement_args`
	- Expressions: 
		- `expression`
		- `expression_args`
	- HTML Elements: 
		- `tag`
		- `tag_attribute`
		- `tag_attribute_equal`





# API: `TokenStore`

## Methods

### Get a new token (`Node`)

Name: `getToken`

#### Parameters

1. `type`
	- type: `String`
	- __required__
	- The type of the token
1. `offset`
	- type: `int`
	- __optional__
	- The offset of the token
1. `length`
	- type: `int`
	- __optional__
	- The length of the token

#### Return

- type: `Node`
- A __new instance__ of the `Node` token, with given `offset` and `length` if provided, and with given `type`. It also sets display information corresponding to the given type, if any.

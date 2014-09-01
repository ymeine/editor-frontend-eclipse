Highlighting



This package handles everything related to syntax highlighting of source code.





# Introduction

## What is syntax highlighting?

First, you can refer to [this article on Wikipedia](http://en.wikipedia.org/wiki/Syntax_highlighting).

The idea is to reflect semantic parts of a source code through colors.

## How does it work?

First let's recap the model around a highlighted content, before explaining the model of the highlighting itself which comes on top of it.

### The model of the content

Let's start form the beginning.

We first have what we call __documents__. A document is a __model__ similar to a text file, but focusing only on what it contains instead of additionally dealing with how to store it, access it, and so on.

In our case, documents contain __text__. A text ([formal definition](https://en.wikipedia.org/wiki/Text_(literary_theory)), [_text file_ definition](https://en.wikipedia.org/wiki/Text_file)) is one __representation__ of the content of a document. We can define it as: a sequence a characters.

However, this representation is too generic, and someone/something reading this text will have to perform additional processing in order to make use of it.

That's why we have an additional __model__, adding semantics, which is the [_(source) code_](https://en.wikipedia.org/wiki/Source_code). This is however rather complex and source code can be associated to __several__ other __models and representations__. We'll explain the main one though.

Finally, a source code is just a human representation of a [_program_](https://en.wikipedia.org/wiki/Computer_program).

### The model of a source code

In order to make use of source code, one will have to __create its model__, __from the underlying model__, __which is a sequence of characters__.

We call this process [___parsing___](https://en.wikipedia.org/wiki/Parsing).

Parsing can produce and/or be related to several representations/models of the source code, we usually refer to three of them, using each other to build themselves: 

1. [Tokens](https://en.wikipedia.org/wiki/Lexical_analysis#Token) sequence: [lexical analysis](https://en.wikipedia.org/wiki/Lexical_analysis#Token), we can say it transforms the sequence of characters to a sequence of tokens, a token being itself a sequence of consecutive characters. However, tokens have more semantic.
2. [__A__btract __S__yntax __T__ree](https://en.wikipedia.org/wiki/Abstract_syntax_tree): [syntactic analysis/parsing](https://en.wikipedia.org/wiki/Parsing), it creates a [tree](https://en.wikipedia.org/wiki/Tree_%28graph_theory%29) (or [graph](https://en.wikipedia.org/wiki/Graph_theory) with similar traits) representing the syntax of the code. This is done thanks to the tokens, but this isn't necessarily a one to one match.
3. [__A__bstract __S__emantic __G__raph](https://en.wikipedia.org/wiki/Abstract_semantic_graph): rarer, adds more semantic on top of the syntax tree.

__The above is part of the common practices but we don't stick to it as is__, please read the following remarks.

Why do we use a tree for the syntax? Because we have a progressive hence hierarchical level of details of how a source code is structured. Therefore children of a given node will tell with more detail how the node is structured.

Usually, only the tokens and the AST are used, the ASG being more marginal and maybe too specific although close to the AST.

Then, tokens don't really represent the source code, they are just a better way to represent the text making the source code. They will represent things that can be essential to build the actual model of the code (the AST), such as white spaces, but that can also be dropped because considered irrelevant.

### Our model of a source code

Our ideal model don't bother with intermediate representation or anything. It tends to be exhaustive and flexible.

It is based on the AST principle, but addressing the following concerns: 

- usual AST are closer to the representation of a program rather than the one of the source code. A source code can be represented as a text, so our representation should not drop any of its information, such as white spaces and so on (see a source code as being a [serialized](https://en.wikipedia.org/wiki/Serialization) form of a program).
- an AST, as its name stands, is a _tree_. The structure is indeed hierarchical and does not require linking foreign nodes. However, we can add some convenient links, such as `parent` and so on, which turn it into a _graph_.

Otherwise, this is as an AST: 

- a __node describes what a portion of source code represents__
- its __children represent more specific portions__ of this whole portion
- __leaves are equivalent to tokens__ (we don't go deeper by representing each character with a node, but theoretically, we could)

### The model of highlighting

Highlighting adds colors to each node of our graph representing source code.

In order not to pollute visual space, overlapping is avoided, so naturally only leaves have highlighting information.





# Documentation

This package implements the highlighting concepts by using the Eclipse API. This means that models described above have to be adapted.

For now, it handles highlights only Aria Templates' Atlas templates.

## The Eclipse model

Eclipse uses a _scanner_ to parse some portions of a document in order to update its highlighting.

The scanner expects tokens to be returned on a scanning operation, each token embedding highlighting information.

In order to produce tokens, there are several possibilities: 

- extract it from a model produced by an external entity (the backend for instance)
- create them by parsing the scanned document's portion, using the Eclipse API

While the first solution is the best one regarding design, the second one is applied here for performances purposes. We will move to the first one when the backend can provide as much or close efficiency.

### The scanner

The scanner that is used by the source viewer configuration is an instance of [`SpecificRuleBasedScanner.java`](./SpecificRuleBasedScanner.java). A set of predefined rules are used to tokenize the editor's content.
Tokens have a tree structure that the scanner is able to flatten in order to provide the correct tokens and the correct offsets and lengths for each of them.

The scanner can also be used within rules in order to tokenize a portion of the document, thus allowing a recursive analysis of the source and return the corresponding hierarchical structure of tokens.

## Getting tokens using the backend

## Getting tokens using the Eclipse API

### Rules

Rules are provided by the singleton [`RulesStore.java`](./RulesStore.java). There are a set of rules that can be retrieved. In particular, two predefined sets of rules are available:

- the default rules to parse the whole document
- a set of rules to parse primitive JavaScript types, such as booleans, strings, numbers, objects and arrays

Complex rules are based on class  [`ContainerRule.java`](./ContainerRule.java), which returns a container token with sub-tokens as children. In some of them an instance of the above-mentioned scanner is used in order to have a recursive parsing of the content.

## Tokens

Tokens are provided by the singleton [`TokensStore.java`](./TokensStore.java). Each of them comes with a color that is used to highlight the corresponding content. Colors are hard-coded inside this class.
Each token is an instance of [`RichToken.java`](./RichToken.java), which enhances the base class provided by existing packages in order to add features (like a children property that allows nested tokens).

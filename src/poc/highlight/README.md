Editor view of a document.

# Documentation

This package contains classes dealing with syntax highlighting for templates.


## Scanner

The scanner that is used by the source viewer configuration is an instance of [`SpecificRuleBasedScanner.java`](./SpecificRuleBasedScanner.java). A set of predefined rules are used to tokenize the editor's content.
Tokens have a tree structure that the scanner is able to flatten in order to provide the correct tokens and the correct offsets and lengths for each of them.

The scanner can also be used within rules in order to tokenize a portion of the document, thus allowing a recursive analysis of the source and return the corresponding hierarchical structure of tokens.


## Rules

Rules are provided by the singleton [`RulesStore.java`](./RulesStore.java). There are a set of rules that can be retrieved. In particular, two predefined sets of rules are available:

* the default rules to parse the whole document
* a set of rules to parse primitive JavaScript types, such as booleans, strings, numbers, objects and arrays

Complex rules are based on class  [`ContainerRule.java`](./ContainerRule.java), which returns a container token with sub-tokens as children. In some of them an instance of the above-mentioned scanner is used in order to have a recursive parsing of the content.


## Tokens

Tokens are provided by the singleton [`TokensStore.java`](./TokensStore.java). Each of them comes with a color that is used to highlight the corresponding content. Colors are hard-coded inside this class.
Each token is an instance of [`RichToken.java`](./RichToken.java), which enhances the base class provided by existing packages in order to add features (like a children property that allows nested tokens).


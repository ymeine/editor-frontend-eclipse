package com.ariatemplates.tools.ide.modes.athtml.highlighting



import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.rules.IRule
import org.eclipse.jface.text.rules.IToken
import org.eclipse.jface.text.rules.RuleBasedScanner

import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich
import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore



/**
 * This class provides an implementation of the RuleBasedScanner which supports
 * a hierarchy of nested tokens. It can also be used in order to parse a
 * fragment of the document
 *
 * @author flongo
 */
class SpecificRuleBasedScanner extends RuleBasedScanner {

	private tokenStore = TokensStore.get()
	private rulesStore = RulesStore.get()

	private currentToken
	private defaultTokenType

	private initialOffset = 0

	private iteratorsStack = []



	def SpecificRuleBasedScanner(defaultTokenType=null, rules=null, document=null, offset=null, length=null) {
		if (defaultTokenType == null) {
			defaultTokenType = TokensStore.DEFAULT
		} else {
			this.defaultTokenType = defaultTokenType
		}

		if (rules == null || rules instanceof int[]) {
			rules = this.rulesStore.getRules(rules)
		}

		// ---------------------------------------------------------------------

		if (offset != null) {
			this.initialOffset = offset
		}

		this.setDefaultReturnToken(this.tokenStore.getToken(defaultTokenType))
		this.setRules(rules as IRule[])

		if (document != null && offset != null) {
			length = length ?: document.getLength() - offset
			this.setRange(document, offset, length)
		}
	}



	/**
	 * It overrides the parent's implementation in order to be able to handle a
	 * tree of tokens
	 *
	 * @return the next token
	 */
	IToken nextToken() {
		def next
		def iteratorsStack = this.iteratorsStack

		if (iteratorsStack.isEmpty()) {
			next = super.nextToken()
		} else {
			def lastIndex = iteratorsStack.size() - 1
			def lastIterator = iteratorsStack[lastIndex]

			if (lastIterator.hasNext()) {
				next = lastIterator++
			} else {
				iteratorsStack.remove(lastIndex)
				next = this.nextToken()
			}
		}

		def enhancedToken = next instanceof Rich ? next : null

		if (enhancedToken != null && enhancedToken.hasChildren()) {
			iteratorsStack += enhancedToken.getChildren().iterator()
			next = this.nextToken()
		}

		if (next.isUndefined()) {
			return this.nextToken()
		}
		this.currentToken = next

		next
	}

	/**
	 * It returns the next token independently of its children. It does not
	 * flatten the hierarchy
	 */
	IToken nextFlatToken() {
		this.currentToken = super.nextToken()
	}



	int getTokenOffset() {
		if (this.currentToken == null) {
			return 0
		}

		def offset = -1

		if (this.currentToken instanceof Rich) {
			offset = this.currentToken.offset
		}

		if (offset == -1) {
			offset = super.getTokenOffset()
		}

		offset
	}

	int getTokenLength() {
		if (this.currentToken == null) {
			return 0
		}

		def length = -1

		if (this.currentToken instanceof Rich) {
			length = this.currentToken.length
		}

		if (length == -1) {
			length = super.getTokenLength()
		}

		length
	}

	def getCurrentOffset() {this.fOffset}
	def getDocument() {this.fDocument}

	/**
	 * It returns the list of token that can be obtained with the given rules.
	 * It can be used as a direct API when the scanner is used programmatically
	 * to parse portions of the document. If the stopAtDefault argument is set
	 * to true, the parser will stop when the default token is found.
	 *
	 * @param stopAtDefault
	 * @return
	 */
	def getToken(stopAtDefault=false) {
		def containerToken = this.tokenStore.getToken(TokensStore.CONTAINER)

		while (this.fOffset < this.fRangeEnd) {
			def initialOffset = this.fOffset
			def finalOffset = this.fOffset
			def next = this.nextFlatToken().clone()

			if (next.length == Rich.UNDEFINED_INT && next.offset == Rich.UNDEFINED_INT) {
				next.offset = initialOffset
				next.length = finalOffset - initialOffset
			}
			if (stopAtDefault && next.type == this.defaultTokenType) {
				return containerToken
			}

			this.currentToken = next
			containerToken.addChild this.currentToken
		}

		containerToken
	}

	/**
	 * @return the number of characters that have been tokenized plus the first
	 *         one that has been associated to the default token, in case the
	 *         getToken method is called with a true argument
	 */
	def getTokenizedLength() {this.getCurrentOffset() - this.initialOffset}
}

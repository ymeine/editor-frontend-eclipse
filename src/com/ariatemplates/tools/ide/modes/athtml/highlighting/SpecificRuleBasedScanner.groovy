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
 *
 */
class SpecificRuleBasedScanner extends RuleBasedScanner {

	private tokenStore = TokensStore.get()
	private rulesStore = RulesStore.get()
	private iteratorsStack = new ArrayList<Iterator<IToken>>()
	private currentToken
	private int initialOffset = 0
	private defaultTokenType



	def SpecificRuleBasedScanner() {
		this.setDefaultReturnToken this.tokenStore.getToken(TokensStore.DEFAULT)
		this.setRules(this.rulesStore.getRules())
	}

	def SpecificRuleBasedScanner(String defaultToken, int[] rulesTypes, IDocument document, int offset, int length) {
		this(defaultToken, RulesStore.get().getRules(rulesTypes), document, offset, length)
	}

	def SpecificRuleBasedScanner(String defaultToken, List<IRule> rules, IDocument document, int offset, int length) {
		this.initialOffset = offset
		this.defaultTokenType = defaultToken
		this.setDefaultReturnToken(this.tokenStore.getToken(defaultToken))
		IRule[] typeArray = new IRule[rules.size()]
		this.setRules(rules.toArray(typeArray))
		this.setRange(document, offset, length)
	}

	public SpecificRuleBasedScanner(String defaultToken, int[] rulesTypes, IDocument document, int offset) {
		this(defaultToken, rulesTypes, document, offset, document.getLength() - offset)
	}

	public SpecificRuleBasedScanner(String defaultToken, List<IRule> rules, IDocument document, int offset) {
		this(defaultToken, rules, document, offset, document.getLength() - offset)
	}



	/**
	 * It overrides the parent's implementation in order to be able to handle a
	 * tree of tokens
	 *
	 * @return the next token
	 */
	IToken nextToken() {
		def temporaryToken

		if (this.iteratorsStack.isEmpty()) {
			temporaryToken = super.nextToken()
		} else {
			def lastIndex = this.iteratorsStack.size() - 1
			def lastIterator = this.iteratorsStack.get(lastIndex)
			if (lastIterator.hasNext()) {
				temporaryToken = lastIterator++
			} else {
				this.iteratorsStack.remove(lastIndex)
				temporaryToken = this.nextToken()
			}
		}

		def enhancedToken = (temporaryToken instanceof Rich) ? temporaryToken : null

		if (enhancedToken != null && enhancedToken.hasChildren()) {
			this.iteratorsStack += enhancedToken.getChildren().iterator()
			temporaryToken = this.nextToken()
		}

		if (temporaryToken.isUndefined()) {
			return this.nextToken()
		}
		this.currentToken = temporaryToken

		temporaryToken
	}

	/**
	 * It returns the next token independently of its children. It does not
	 * flatten the hierarchy
	 *
	 * @return
	 */
	IToken nextFlatToken() {
		def temporaryToken

		temporaryToken = super.nextToken()
		this.currentToken = temporaryToken

		temporaryToken
	}

	int getTokenOffset() {
		if (this.currentToken == null) {
			return 0
		}

		def offset = -1
		if (this.currentToken instanceof Rich) {
			def enhancedToken = this.currentToken
			offset = enhancedToken.getOffset()
		}
		if (offset == -1) {
			return super.getTokenOffset()
		}

		offset
	}

	int getTokenLength() {
		if (this.currentToken == null) {
			return 0
		}

		int length = -1
		if (this.currentToken instanceof Rich) {
			def enhancedToken = this.currentToken
			length = enhancedToken.getLength()
		}
		if (length == -1) {
			return super.getTokenLength()
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
		containerToken = this.tokenStore.getToken(TokensStore.CONTAINER)

		while (this.fOffset < this.fRangeEnd) {
			int initialOffset = this.fOffset
			def next = this.nextFlatToken()
			int finalOffset = this.fOffset
			def pocNext = next
			def pocNextClone = pocNext.clone()

			if (pocNextClone.getLength() == Rich.UNDEFINED_INT && pocNextClone.getOffset() == Rich.UNDEFINED_INT) {
				pocNextClone.setOffset initialOffset
				pocNextClone.setLength(finalOffset - initialOffset)
			}
			if (stopAtDefault && pocNextClone.getType() == this.defaultTokenType) {
				return containerToken
			}

			this.currentToken = pocNextClone
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

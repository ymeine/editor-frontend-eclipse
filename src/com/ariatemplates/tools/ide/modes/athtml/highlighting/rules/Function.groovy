package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IRule
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Function extends Container {

	static final KEYWORD = "function"
	static final KEYWORD_LENGTH = this.KEYWORD.size()

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner
		def cl = this.class
		def tokenStore = this.tokenStore

		this.read cl.KEYWORD_LENGTH

		if (!(this.buffer == cl.KEYWORD)) {
			this.rewind()
			return Rich.UNDEFINED
		}

		this.addToken tokenStore.getToken(
			TokensStore.FUNCTION,
			this.start,
			cl.KEYWORD_LENGTH
		)

		def toContinue = this.parseFunctionArguments()
		if (!toContinue) {
			return this.containerToken
		}

		this.parseFunctionBody()

		this.containerToken
	}

	private parseFunctionArguments() {
		def KEYWORD_LENGTH = this.class.KEYWORD_LENGTH
		def argsOffset = KEYWORD_LENGTH
		def next = 0

		while (next != ICharacterScanner.EOF && next != ')') {
			next = this.read()

			if (next == '(') {
				this.addToken tokenStore.getToken(
					TokensStore.FUNCTION,
					this.start + KEYWORD_LENGTH,
					this.offset - 7
				)

				argsOffset = this.offset + 1
			}
		}

		if (next == ICharacterScanner.EOF) {
			this.unread()

			if (argsOffset < this.offset) {
				this.addToken tokenStore.getToken(
					TokensStore.DEFAULT,
					this.start + argsOffset,
					this.offset - argsOffset
				)
			}

			return false
		} else {
			if (argsOffset < this.offset) {
				this.addToken tokenStore.getToken(
					TokensStore.DEFAULT,
					this.start + argsOffset,
					this.offset - argsOffset
				)
			}
			this.addToken tokenStore.getToken(
				TokensStore.FUNCTION,
				this.start + this.offset,
				1
			)
		}

		true
	}

	private void parseFunctionBody() {
		def initialOffset = this.offset
		def bracketsCount = 0
		def next = 0

		while (next != ICharacterScanner.EOF && next != '{') {
			next = this.read()
		}

		if (next == ICharacterScanner.EOF) {
			this.unread()

			this.addToken tokenStore.getToken(
				TokensStore.FUNCTION,
				initialOffset,
				this.offset - initialOffset
			)

			return
		} else {
			this.addToken tokenStore.getToken(
				TokensStore.FUNCTION,
				initialOffset,
				this.offset - initialOffset
			)

			bracketsCount++
		}

		def rules = RulesStore.get().primitiveRules
		def document = this.scanner.getDocument()
		while (next != ICharacterScanner.EOF && bracketsCount > 0) {
			next = this.read()

			def subscanner = new SpecificRuleBasedScanner(
				TokensStore.DEFAULT,
				rules,
				document,
				this.start + this.offset
			)

			def nextToken = subscanner.getToken true
			def tokenizedLentgh = subscanner.tokenizedLength - 1

			if (tokenizedLentgh > 0) {
				this.addToken nextToken
				this.read(tokenizedLentgh - 1)
			} else {
				if (next == '{') {
					bracketsCount++;
				} else (next == '}') {
					bracketsCount--;
				}

				if (bracketsCount == 0) {
					this.addToken tokenStore.getToken(
						TokensStore.FUNCTION,
						this.start + this.offset,
						1
					)
				} else {
					this.addToken tokenStore.getToken(
						TokensStore.DEFAULT,
						this.start + this.offset,
						1
					)
				}
			}
		}
		if (next == ICharacterScanner.EOF) {
			this.unread()
		}
	}

}

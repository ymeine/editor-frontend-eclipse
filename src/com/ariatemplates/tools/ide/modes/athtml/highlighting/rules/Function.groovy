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

		this.read cl.KEYWORD_LENGTH

		if (!(this.buffer == cl.KEYWORD)) {
			this.rewind()
			return Rich.UNDEFINED
		}

		this.addToken this.tokenStore.getToken(
			TokensStore.FUNCTION,
			this.start,
			cl.KEYWORD_LENGTH
		)

		boolean toContinue = this.parseFunctionArguments()
		if (!toContinue) {
			return this.containerToken
		}

		this.parseFunctionBody()

		this.containerToken
	}

	def parseFunctionArguments() {
		def KEYWORD_LENGTH = this.class.KEYWORD_LENGTH
		int argsOffset = KEYWORD_LENGTH
		int next = 0

		while (next != ICharacterScanner.EOF && next != ')') {
			next = this.read()

			if (next == '(') {
				this.addToken this.tokenStore.getToken(
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
				this.addToken this.tokenStore.getToken(
					TokensStore.DEFAULT,
					this.start + argsOffset,
					this.offset - argsOffset
				)
			}

			return false
		} else {
			if (argsOffset < this.offset) {
				this.addToken this.tokenStore.getToken(
					TokensStore.DEFAULT,
					this.start + argsOffset,
					this.offset - argsOffset
				)
			}
			this.addToken this.tokenStore.getToken(
				TokensStore.FUNCTION,
				this.start + this.offset,
				1
			)
		}

		true
	}

	private void parseFunctionBody() {
		int initialOffset = this.offset
		int bracketsCount = 0
		int next = 0

		while (next != ICharacterScanner.EOF && next != '{') {
			next = this.read()
		}
		if (next == ICharacterScanner.EOF) {
			this.unread()

			this.addToken this.tokenStore.getToken(
				TokensStore.FUNCTION,
				initialOffset,
				this.offset - initialOffset
			)

			return
		} else {
			this.addToken this.tokenStore.getToken(
				TokensStore.FUNCTION,
				initialOffset,
				this.offset - initialOffset
			)

			bracketsCount++
		}

		def rules = RulesStore.get().getPrimitiveRules()
		while (next != ICharacterScanner.EOF && bracketsCount > 0) {
			next = this.read()

			def subscanner = new SpecificRuleBasedScanner(
				TokensStore.DEFAULT,
				rules,
				this.scanner.getDocument(),
				this.start + this.offset
			)

			def nextToken = subscanner.getToken true
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1

			if (tokenizedLentgh > 0) {
				this.addToken nextToken
				this.read(tokenizedLentgh - 1)
			} else {
				if (next == '{') {
					bracketsCount++;
				}

				if (next == '}') {
					bracketsCount--;
				}

				if (bracketsCount == 0) {
					this.addToken this.tokenStore.getToken(
						TokensStore.FUNCTION,
						this.start + this.offset,
						1
					)
				} else {
					this.addToken this.tokenStore.getToken(
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

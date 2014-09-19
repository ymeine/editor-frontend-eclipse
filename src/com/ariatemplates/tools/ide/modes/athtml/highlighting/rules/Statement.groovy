package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IRule
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Statement extends Container {

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner
		def tokenStore = this.tokenStore

		def bracketsCount = 0

		def next = this.read()

		if (next != '{' || next == ICharacterScanner.EOF) {
			this.rewind()
			return Rich.UNDEFINED
		}

		bracketsCount++
		this.__addToken()

		next = 0
		def isStatementnameOver = false
		def rules = RulesStore.get().primitiveRules
		def document = this.scanner.document

		while (next != ICharacterScanner.EOF && bracketsCount > 0) {
			next = this.read()

			def subscanner = new SpecificRuleBasedScanner(
				TokensStore.DEFAULT,
				rules,
				document,
				this.start + this.offset
			);

			def nextToken = subscanner.getToken true
			def tokenizedLentgh = subscanner.tokenizedLength - 1

			if (tokenizedLentgh > 0) {
				isStatementnameOver = true
				this.addToken nextToken
				this.read(tokenizedLentgh - 1)
			} else {
				if (next == ' ') {
					isStatementnameOver = true
				}

				if (next == '{' && this.buffer.charAt(this.offset - 1) != '\\') {
					isStatementnameOver = true
					bracketsCount++
				}

				if (next == '}' && this.buffer.charAt(this.offset - 1) != '\\') {
					isStatementnameOver = true
					bracketsCount--
				}

				if (bracketsCount == 0) {
					if (this.buffer.charAt(this.offset - 1) == '/') {
						this.removeLastToken()

						this.addToken tokenStore.getToken(
							TokensStore.STATEMENT,
							this.start + this.offset - 1,
							2
						)
					} else {
						this.__addToken()
					}
				} else if (next != ICharacterScanner.EOF) {
					if (!isStatementnameOver) {
						this.__addToken()
					} else {
						this.addToken tokenStore.getToken(
							TokensStore.STATEMENT_ARGS,
							this.start + this.offset,
							1
						)
					}
				}
			}
		}
		if (next == ICharacterScanner.EOF) {
			this.unread()
		}

		this.containerToken
	}

	private __addToken() {
		this.addToken this.@tokenStore.getToken(
			TokensStore.STATEMENT,
			this.start + this.offset,
			1
		)
	}
}

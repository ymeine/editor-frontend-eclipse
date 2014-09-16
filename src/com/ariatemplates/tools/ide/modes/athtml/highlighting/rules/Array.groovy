package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IRule
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Array extends Container {

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner

		int next = this.read()
		char nextChar = next

		if (nextChar != '[' || next == ICharacterScanner.EOF) {
			this.rewind()
			return Rich.UNDEFINED
		}

		this.__addToken()

		next = 0
		def isArrayOver = false
		def valueRules = RulesStore.get().getPrimitiveRules()
		int tokenizedLentgh = 0
		def nextToken

		while (next != ICharacterScanner.EOF && !isArrayOver) {
			next = this.read()
			nextChar = next

			def subscanner = new SpecificRuleBasedScanner(
				TokensStore.DEFAULT,
				valueRules,
				this.scanner.getDocument(),
				this.start + this.offset
			)

			nextToken = subscanner.getToken true
			tokenizedLentgh = subscanner.getTokenizedLength() - 1

			if (tokenizedLentgh > 0) {
				this.addToken nextToken
				this.read(tokenizedLentgh - 1)
			} else {
				if (nextChar == ']') {
					isArrayOver = true
				}

				this.__addToken()
			}
		}

		if (next == ICharacterScanner.EOF) {
			this.unread()
		}

		this.containerToken
	}

	private __addToken() {
		this.addToken this.tokenStore.getToken(
			TokensStore.ARRAY,
			this.start + this.offset,
			1
		)
	}
}

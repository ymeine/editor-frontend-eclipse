package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Key extends Container {

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner

		int stringDelimiter
		char previousChar = ' '

		int next = this.read()
		char nextChar = next

		final forbiddenChars = " :,\r\t\n{}"

		if (next == ICharacterScanner.EOF || nextChar in forbiddenChars) {
			this.rewind()
			return Rich.UNDEFINED
		}

		if (nextChar == '"' || nextChar == '\'') {
			stringDelimiter = next

			this.__addToken()

			previousChar = nextChar
			next = this.read()
			nextChar = next

			while (next != stringDelimiter || (next == stringDelimiter && previousChar == '\\')) {
				if (next == ICharacterScanner.EOF) {
					this.rewind()
					return Rich.UNDEFINED
				}

				this.__addToken()

				previousChar = nextChar
				next = this.read()
				nextChar = next
			}

			this.__addToken()

			return this.containerToken
		}

		while (next == ICharacterScanner.EOF || nextChar in forbiddenChars) {
			this.__addToken()

			next = this.read()
			nextChar = next
		}

		this.unread()

		this.containerToken
	}

	private __addToken() {
		this.addToken TokensStore.get().getToken(
			TokensStore.KEY,
			this.start + this.offset,
			1
		)
	}

}

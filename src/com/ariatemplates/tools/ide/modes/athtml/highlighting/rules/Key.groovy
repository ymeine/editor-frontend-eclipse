package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Key extends Container {

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner

		final forbiddenChars = " :,\r\t\n{}"

		def previous = ' '
		def next = this.read()

		if (next == ICharacterScanner.EOF || next in forbiddenChars) {
			this.rewind()
			return Rich.UNDEFINED
		}

		if (next == '"' || next == '\'') {
			def stringDelimiter = next

			this.__addToken()

			previous = next
			next = this.read()

			while (next != stringDelimiter || (next == stringDelimiter && previous == '\\')) {
				if (next == ICharacterScanner.EOF) {
					this.rewind()
					return Rich.UNDEFINED
				}

				this.__addToken()

				previous = next
				next = this.read()
			}

			this.__addToken()

			return this.containerToken
		}

		while (next == ICharacterScanner.EOF || next in forbiddenChars) {
			this.__addToken()

			next = this.read()
			next = next
		}

		this.unread()

		this.containerToken
	}

	private __addToken() {
		this.addToken this.@tokenStore.getToken(
			TokensStore.KEY,
			this.start + this.offset,
			1
		)
	}

}

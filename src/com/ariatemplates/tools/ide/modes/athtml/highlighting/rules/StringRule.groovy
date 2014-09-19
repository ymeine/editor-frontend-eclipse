package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



public class StringRule extends Container {

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner
		def tokenStore = this.tokenStore

		def next = this.read()

		if ((next != '"' && next != '\'') || next == ICharacterScanner.EOF) {
			this.rewind()
			return Rich.UNDEFINED
		}
		def delimiter = next

		this.addToken tokenStore.getToken(
			TokensStore.STRING,
			this.start + this.offset,
			1
		)

		def previous = next
		next = this.read()

		def rulesTypes = [
			RulesStore.STATEMENT,
			RulesStore.EXPRESSION
		]
		def document = this.scanner.document

		while (next != delimiter || (next == delimiter && previous == '\\')) {
			def subscanner = new SpecificRuleBasedScanner(
				TokensStore.STRING,
				rulesTypes,
				document,
				this.start + this.offset
			)

			def nextToken = subscanner.getToken true
			def tokenizedLentgh = subscanner.tokenizedLength - 1

			if (tokenizedLentgh > 0) {
				this.addToken nextToken
				this.read(tokenizedLentgh - 1)
			} else {
				if (next == ICharacterScanner.EOF) {
					this.rewind()
					return Rich.UNDEFINED
				}
				this.addToken tokenStore.getToken(
					TokensStore.STRING,
					this.start + this.offset,
					1
				)
			}

			previous = next
			next = this.read()
		}

		this.addToken tokenStore.getToken(
			TokensStore.STRING,
			this.start + this.offset,
			1
		)

		this.containerToken
	}
}

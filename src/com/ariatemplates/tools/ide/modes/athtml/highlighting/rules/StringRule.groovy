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

		def stringDelimiter
		def previousChar = ' '

		def next = this.read()

		if ((next != '"' && next != '\'') || next == ICharacterScanner.EOF) {
			this.rewind()
			return Rich.UNDEFINED
		}

		stringDelimiter = next

		def rulesTypes = [
			RulesStore.STATEMENT,
			RulesStore.EXPRESSION
		]

		this.addToken TokensStore.get().getToken(
			TokensStore.STRING,
			this.start + this.offset,
			1
		)

		previousChar = next
		next = this.read()

		while (next != stringDelimiter || (next == stringDelimiter && previousChar == '\\')) {
			def subscanner = new SpecificRuleBasedScanner(
				TokensStore.STRING,
				rulesTypes,
				this.scanner.getDocument(),
				this.start + this.offset
			)

			def nextToken = subscanner.getToken true
			def tokenizedLentgh = subscanner.getTokenizedLength() - 1

			if (tokenizedLentgh > 0) {
				this.addToken nextToken
				this.read(tokenizedLentgh - 1)
				previousChar = ' '
			} else {
				if (next == ICharacterScanner.EOF) {
					this.rewind();
					return Rich.UNDEFINED;
				}
				this.addToken TokensStore.get().getToken(
					TokensStore.STRING,
					this.start + this.offset,
					1
				)
			}

			previousChar = next
			next = this.read()
		}

		this.addToken TokensStore.get().getToken(
			TokensStore.STRING,
			this.start + this.offset,
			1
		)

		this.containerToken
	}
}

package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Tag extends Container {

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner

		int next = this.read()

		if (next != '<' || next == ICharacterScanner.EOF) {
			this.rewind()
			return Rich.UNDEFINED
		}

		while (next != ' ' && next != '>' && next != ICharacterScanner.EOF) {
			next = this.read()
		}

		this.addToken TokensStore.get().getToken(
			TokensStore.TAG,
			this.start,
			this.buffer.length() - 1
		)

		def rulesTypes = [
			RulesStore.STATEMENT,
			RulesStore.EXPRESSION,
			RulesStore.STRING_COMPLEX,
			RulesStore.TAG_ATTRIBUTE
		]

		while (next != ICharacterScanner.EOF) {
			def subscanner = new SpecificRuleBasedScanner(
				TokensStore.DEFAULT,
				rulesTypes,
				this.scanner.getDocument(),
				this.start + this.offset
			)
			def nextToken = subscanner.getToken true
			def tokenizedLentgh = subscanner.getTokenizedLength() - 1

			if (tokenizedLentgh > 0) {
				this.addToken nextToken
				this.read(tokenizedLentgh - 1)
			} else {
				if (next == '>' || next == '/') {
					this.addToken TokensStore.get().getToken(
						TokensStore.TAG,
						this.start + this.offset,
						1
					)

					if (next == '>') {
						return this.containerToken
					}
				} else {
					this.addToken TokensStore.get().getToken(
						TokensStore.DEFAULT,
						this.start + this.offset,
						1
					)
				}
			}
			next = this.read()
		}

		this.containerToken
	}

}

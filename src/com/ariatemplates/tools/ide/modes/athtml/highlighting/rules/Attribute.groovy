package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Attribute extends Container {

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner

		char charNext = this.read()

		if (this.buffer.matches("[\\w-_:\\s=]")) {
			if (charNext == '=') {
				return TokensStore.get().getToken(
					TokensStore.TAG_ATTRIBUTE_EQUAL,
					this.start + this.offset,
					1
				)
			} else {
				return TokensStore.get().getToken(
					TokensStore.TAG_ATTRIBUTE,
					this.start + this.offset,
					1
				)
			}
		}

		this.rewind()

		Rich.UNDEFINED
	}

}

package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Attribute extends Container {

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner

		def next = this.read()

		if (this.buffer.matches("[\\w-_:\\s=]")) {
			def tokenType = next == '=' ? TokensStore.TAG_ATTRIBUTE_EQUAL : TokensStore.TAG_ATTRIBUTE

			return this.@tokenStore.getToken(
				tokenType,
				this.start + this.offset,
				1
			)
		}

		this.rewind()

		Rich.UNDEFINED
	}

}

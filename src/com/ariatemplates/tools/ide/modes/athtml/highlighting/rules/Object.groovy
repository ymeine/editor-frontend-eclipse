package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IRule
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Object extends Container {

	private static LOOKING_FOR_KEY = 0
	private static LOOKING_FOR_VALUE = 1
	private static LOOKING_FOR_NOTHING = 2

	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner
		def cl = this.class
		def tokenStore = this.tokenStore

		def next = this.read()

		if (next != '{' || next == ICharacterScanner.EOF) {
			this.rewind()
			return Rich.UNDEFINED
		}

		this.addToken tokenStore.getToken(
			TokensStore.OBJECT,
			this.start + this.offset,
			1
		)

		next = 0

		def isObjectOver = false
		def status = cl.LOOKING_FOR_KEY

		def rulesStore = RulesStore.get()
		def valueRules = rulesStore.primitiveRules
		def keyRulesTypes = [RulesStore.KEY]
		def keyRules = rulesStore.getRules keyRulesTypes
		def document = this.scanner.document

		def tokenizedLentgh = 0
		def nextToken

		while (next != ICharacterScanner.EOF && !isObjectOver) {
			next = this.read()

			def rules
			if (status == cl.LOOKING_FOR_KEY) {
				rules = keyRules
			} else if (status == cl.LOOKING_FOR_VALUE) {
				rules = valueRules
			}

			if (rules != null) {
				def subscanner = new SpecificRuleBasedScanner(
					TokensStore.DEFAULT,
					rules,
					document,
					this.start + this.offset
				);

				nextToken = subscanner.getToken true
				tokenizedLentgh = subscanner.tokenizedLength - 1
			} else {
				tokenizedLentgh = 0
			}

			if (tokenizedLentgh > 0) {
				this.addToken nextToken
				this.read(tokenizedLentgh - 1)

				if (status == cl.LOOKING_FOR_KEY) {
					status = cl.LOOKING_FOR_NOTHING
				}
			} else {
				switch (next) {
					case ':':
						status = cl.LOOKING_FOR_VALUE
						break
					case ',':
						status = cl.LOOKING_FOR_KEY
						break
					case '}':
						isObjectOver = true
						break
				}

				this.addToken tokenStore.getToken(
					TokensStore.OBJECT,
					this.start + this.offset,
					1
				)
			}
		}

		if (next == ICharacterScanner.EOF) {
			this.unread()
		}

		this.containerToken
	}

}

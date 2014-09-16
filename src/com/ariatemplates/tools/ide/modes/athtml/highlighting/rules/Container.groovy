package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IRule
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



/**
 * This class is used as a base class for all those rules whose output is a
 * complex token that just contains a nested structure of tokens. It provides
 * some methods that facilitate the creation of the container token.
 *
 * @author flongo
 *
 */
class Container implements IRule {

	protected tokenStore = TokensStore.get()

	protected start = 0
	protected SpecificRuleBasedScanner scanner
	protected buffer = ""
	protected intBuffer = []
	protected offset = -1
	protected Rich containerToken

	/**
	 * Sets some properties that are useful when reading a scanner
	 *
	 * @param initialScanner
	 */
	IToken evaluate(ICharacterScanner initialScanner) {
		this.scanner = initialScanner
		this.start = scanner.getCurrentOffset()
		this.buffer = ""
		this.intBuffer.clear()
		this.offset = -1
		this.containerToken = this.tokenStore.getToken TokensStore.CONTAINER

		null
	}

	protected int read(count=1) {
		int next
		count.times {
			next = this.scanner.read()
			this.offset++
			this.buffer += next
			this.intBuffer += next
		}

		next
	}

	protected unread() {
		this.scanner.unread()
		this.offset--
		this.buffer = this.buffer[0..-1]
		this.intBuffer.remove(this.intBuffer.size() - 1)
	}

	protected rewind() {
		while (this.offset > -1) {
			this.offset--
			this.scanner.unread()
		}
		this.buffer = ""
		this.intBuffer.clear()
	}

	protected addToken(token) {
		this.containerToken.addChild token
	}

	protected removeLastToken() {
		this.containerToken.popChild()
	}
}

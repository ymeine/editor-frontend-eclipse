package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich;



public class Attribute extends Container {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);
		int next = this.read();
		char charNext = (char) next;
		if (this.buffer.matches("[\\w-_:\\s=]")) {
			if (charNext == '=') {
				return TokensStore.get().getToken(TokensStore.TAG_ATTRIBUTE_EQUAL, this.start + this.offset, 1);
			} else {
				return TokensStore.get().getToken(TokensStore.TAG_ATTRIBUTE, this.start + this.offset, 1);
			}
		}
		this.rewind();
		return Rich.UNDEFINED;
	}

}

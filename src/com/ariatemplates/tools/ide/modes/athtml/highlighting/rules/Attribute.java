package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich;



public class Attribute extends Container {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);
		
		char charNext = (char) this.read();
		
		if (this.buffer.matches("[\\w-_:\\s=]")) {
			String tokenType;
			if (charNext == '=') {
				tokenType = TokensStore.TAG_ATTRIBUTE_EQUAL;
			} else {
				tokenType = TokensStore.TAG_ATTRIBUTE;
			}
			
			return this.tokenStore.getToken(
				tokenType,
				this.start + this.offset,
				1
			);
		}
		
		this.rewind();
		
		return Rich.UNDEFINED;
	}

}

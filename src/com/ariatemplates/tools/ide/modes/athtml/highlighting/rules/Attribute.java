package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Attribute extends BaseRule {

	public Attribute() {
		this.ruleName = "Attribute";
		this.__debug__ = true;
	}

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		this.read();

		if (this.buffer.toString().matches("[\\w-_:\\s=]")) {
			String tokenType;
			if (this.current == '=') {
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

		return Node.UNDEFINED;
	}

}

package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Array extends BaseRule {

	public Array() {
		this.ruleName = "Array";
		this.__debug__ = true;
	}

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		if (!this.detectRule()) {
			this.rewind();
			return Node.UNDEFINED;
		}

		boolean isRuleOver;
		do {
			isRuleOver = this.continueParsing();
		} while (!this.isEOF() && !isRuleOver);

		if (this.isEOF()) {
			this.unread();
		}

		return this.containerToken;
	}

	protected boolean detectRule() {
		this.read();
		if (this.current != '[' || this.isEOF()) {
			return false;
		}

		this.addToken(
			TokensStore.ARRAY,
			1
		);

		return true;
	}

	protected boolean continueParsing() {
		boolean isRuleOver = false;

		this.read();

		SpecificRuleBasedScanner subscanner = this.createScanner(
			TokensStore.DEFAULT,
			this.rulesStore.getPrimitiveRules()
		);

		Node nextToken = subscanner.getToken(true);
		int tokenizedLentgh = subscanner.getTokenizedLength() - 1;

		if (tokenizedLentgh > 0) {
			this.addToken(nextToken);
			this.read(tokenizedLentgh - 1);
		} else {
			if (this.current == ']') {
				isRuleOver = true;
			}

			this.addToken(
				TokensStore.ARRAY,
				1
			);
		}

		return isRuleOver;
	}
}

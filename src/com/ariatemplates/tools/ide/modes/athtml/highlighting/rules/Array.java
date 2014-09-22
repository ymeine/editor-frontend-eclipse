package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Array extends Container {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		if (!this.detectRule()) {
			this.rewind();
			return Node.UNDEFINED;
		}

		int next;
		boolean isRuleOver;
		do {
			isRuleOver = this.continueParsing();
			next = this.buffer.charAt(this.buffer.length() - 1);
		} while (next != ICharacterScanner.EOF && !isRuleOver);
		
		if (next == ICharacterScanner.EOF) {
			this.unread();
		}

		return this.containerToken;
	}
	
	protected boolean detectRule() {
		char next = (char) this.read();
		if (next != '[' || next == ICharacterScanner.EOF) {
			return false;
		}
		
		this.addToken(this.tokenStore.getToken(
			TokensStore.ARRAY,
			this.start + this.offset,
			1
		));
		
		return true;
	}

	protected boolean continueParsing() {
		boolean isRuleOver = false;

		char next = (char) this.read();

		SpecificRuleBasedScanner subscanner = new SpecificRuleBasedScanner(
			TokensStore.DEFAULT,
			RulesStore.get().getPrimitiveRules(),
			this.scanner.getDocument(),
			this.start + this.offset
		);
		
		Node nextToken = subscanner.getToken(true);
		int tokenizedLentgh = subscanner.getTokenizedLength() - 1;
		
		if (tokenizedLentgh > 0) {
			this.addToken(nextToken);
			this.read(tokenizedLentgh - 1);
		} else {
			if (next == ']') {
				isRuleOver = true;
			}
			
			this.addToken(this.tokenStore.getToken(
				TokensStore.ARRAY,
				this.start + this.offset,
				1
			));
		}
		
		return isRuleOver;
	}
}

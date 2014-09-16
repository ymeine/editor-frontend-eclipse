package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich;



public class Statement extends Container {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);
		int bracketsCount = 0;

		int next = this.read();
		char nextChar = (char) next;
		if (nextChar != '{' || next == ICharacterScanner.EOF) {
			this.rewind();
			return Rich.UNDEFINED;
		}
		bracketsCount++;
		this.addToken(this.tokenStore.getToken(TokensStore.STATEMENT, this.start + this.offset, 1));

		next = 0;
		boolean isStatementnameOver = false;
		List<IRule> rules = RulesStore.get().getPrimitiveRules();
		while (next != ICharacterScanner.EOF && bracketsCount > 0) {
			next = this.read();
			nextChar = (char) next;
			SpecificRuleBasedScanner subscanner = new SpecificRuleBasedScanner(TokensStore.DEFAULT, rules, this.scanner.getDocument(), this.start + this.offset);
			Rich nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			if (tokenizedLentgh > 0) {
				isStatementnameOver = true;
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
			} else {
				if (nextChar == ' ') {
					isStatementnameOver = true;
				}
				if (nextChar == '{' && this.buffer.charAt(this.offset - 1) != '\\') {
					isStatementnameOver = true;
					bracketsCount++;
				}
				if (nextChar == '}' && this.buffer.charAt(this.offset - 1) != '\\') {
					isStatementnameOver = true;
					bracketsCount--;
				}
				if (bracketsCount == 0) {
					if (this.buffer.charAt(this.offset - 1) == '/') {
						this.removeLastToken();
						this.addToken(this.tokenStore.getToken(TokensStore.STATEMENT, this.start + this.offset - 1, 2));
					} else {
						this.addToken(this.tokenStore.getToken(TokensStore.STATEMENT, this.start + this.offset, 1));
					}
				} else if (next != ICharacterScanner.EOF) {
					if (!isStatementnameOver) {
						this.addToken(this.tokenStore.getToken(TokensStore.STATEMENT, this.start + this.offset, 1));
					} else {
						this.addToken(this.tokenStore.getToken(TokensStore.STATEMENT_ARGS, this.start + this.offset, 1));
					}
				}
			}
		}
		if (next == ICharacterScanner.EOF) {
			this.unread();
		}

		return this.containerToken;

	}
}

package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Statement extends BaseRule {

	private int bracketsCount;
	private boolean isStatementNameOver;

	public Statement() {
		this.ruleName = "Statement";
		this.__debug__ = true;

		this.bracketsCount = 0;
		this.isStatementNameOver = false;
	}

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		// --------------------------------------------------------- detect rule

		this.read();
		if (this.isEOF() || !this.detectRule()) {
			this.rewind();
			return Node.UNDEFINED;
		}

		this.addToken(TokensStore.STATEMENT, 1);

		// ------------------------------------------------ parsing: sub content

		boolean isRuleOver = false;
		do {
			isRuleOver = this.continueParsing();
		} while (!this.isEOF() && !isRuleOver);

		// ------------------------------------------------------ end of parsing

		if (this.isEOF()) {
			this.unread();
		}

		return this.containerToken;
	}

	protected boolean detectRule() {
		if (this.current != '{') {
			return false;
		}

		this.bracketsCount = 1;
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
			this.isStatementNameOver = true;

			this.addToken(nextToken);

			this.read(tokenizedLentgh - 1);
		} else {
			int previous = this.buffer.content.get(this.offset - 1);

			if (this.current == ' ') {
				this.isStatementNameOver = true;
			} else if (this.current == '{' && previous != '\\') {
				this.isStatementNameOver = true;
				this.bracketsCount++;
			} else if (this.current == '}' && previous != '\\') {
				this.isStatementNameOver = true;
				this.bracketsCount--;
			}

			if (this.bracketsCount == 0) {
				isRuleOver = true;

				if (previous == '/') {
					this.removeLastToken();

					this.addToken(
						TokensStore.STATEMENT,
						this.offset - 1,
						2
					);
				} else {
					this.addToken(TokensStore.STATEMENT, 1);
				}
			} else if (!this.isEOF()) {
				if (!this.isStatementNameOver) {
					this.addToken(TokensStore.STATEMENT, 1);
				} else {
					this.addToken(TokensStore.STATEMENT_ARGS, 1);
				}
			}
		}

		return isRuleOver;
	}
}

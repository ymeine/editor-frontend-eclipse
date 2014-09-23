package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Statement extends BaseRule {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		// ---------------------------------------------------------------------

		int bracketsCount = 0;

		this.read();

		if (this.current != '{' || this.isEOF()) {
			this.rewind();
			return Node.UNDEFINED;
		}

		bracketsCount++;
		this.addToken(
			TokensStore.STATEMENT,
			1
		);

		// ---------------------------------------------------------------------

		boolean isStatementnameOver = false;
		List<IRule> rules = this.rulesStore.getPrimitiveRules();

		while (!this.isEOF() && bracketsCount > 0) {
			this.read();

			SpecificRuleBasedScanner subscanner = this.createScanner(
				TokensStore.DEFAULT,
				rules
			);

			Node nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;

			if (tokenizedLentgh > 0) {
				isStatementnameOver = true;

				this.addToken(nextToken);

				this.read(tokenizedLentgh - 1);
			} else {
				if (this.current == ' ') {
					isStatementnameOver = true;
				}
				if (this.current == '{' && this.buffer.content.get(this.offset - 1) != '\\') {
					isStatementnameOver = true;
					bracketsCount++;
				}
				if (this.current == '}' && this.buffer.content.get(this.offset - 1) != '\\') {
					isStatementnameOver = true;
					bracketsCount--;
				}

				if (bracketsCount == 0) {
					if (this.buffer.content.get(this.offset - 1) == '/') {
						this.removeLastToken();

						this.addToken(
							TokensStore.STATEMENT,
							this.offset - 1,
							2
						);
					} else {
						this.addToken(
							TokensStore.STATEMENT,
							1
						);
					}
				} else if (!this.isEOF()) {
					if (!isStatementnameOver) {
						this.addToken(
							TokensStore.STATEMENT,
							1
						);
					} else {
						this.addToken(
							TokensStore.STATEMENT_ARGS,
							1
						);
					}
				}
			}
		}

		if (this.isEOF()) {
			this.unread();
		}

		return this.containerToken;

	}
}

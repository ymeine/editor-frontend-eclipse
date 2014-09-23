package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Expression extends BaseRule {

	public Expression() {
		this.ruleName = "Expression";
		this.__debug__ = true;
	}


	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		int bracketsCount = 0;
		this.read();

		if (this.current != '$' || this.isEOF()) {
			this.rewind();
			return Node.UNDEFINED;
		}

		this.read();

		if (this.current != '{' || this.isEOF()) {
			this.rewind();
			return Node.UNDEFINED;
		}

		bracketsCount++;
		this.addToken(
			TokensStore.EXPRESSION,
			0,
			2
		);

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
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
			} else {
				if (this.current == '{') {
					bracketsCount++;
				}
				if (this.current == '}') {
					bracketsCount--;
				}

				if (bracketsCount == 0) {
					this.addToken(
						TokensStore.EXPRESSION,
						1
					);
				} else if (!this.isEOF()) {
					this.addToken(
						TokensStore.EXPRESSION_ARGS,
						1
					);
				}
			}
		}

		if (this.isEOF()) {
			this.unread();
		}

		return this.containerToken;
	}
}

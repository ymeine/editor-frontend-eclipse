package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class StringRule extends BaseRule {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);



		if (!this.detectRule()) {
			this.rewind();
			return Node.UNDEFINED;
		}
		this.addToken(
			TokensStore.STRING,
			1
		);



		int lastCharacter = this.buffer.last();
		int stringDelimiter = lastCharacter;
		int previous = lastCharacter;

		int[] rulesTypes = {
				RulesStore.STATEMENT,
				RulesStore.EXPRESSION
		};

		this.read();
		while (!(this.current == stringDelimiter && previous != '\\')) {

			SpecificRuleBasedScanner subscanner = this.createScanner(
				TokensStore.STRING,
				rulesTypes
			);

			Node nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;

			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);

				this.read(tokenizedLentgh - 1);
				previous = ' ';
			} else {
				if (this.isEOF()) {
					this.rewind();
					return Node.UNDEFINED;
				}

				this.addToken(
					TokensStore.STRING,
					1
				);
			}

			previous = this.current;
			this.read();
		}

		this.addToken(
			TokensStore.STRING,
			1
		);

		return this.containerToken;
	}

	protected boolean detectRule() {
		this.read();

		if ((this.current != '"' && this.current != '\'') || this.current == ICharacterScanner.EOF) {
			return false;
		}

		return true;
	}
}

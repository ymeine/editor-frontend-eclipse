package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Tag extends BaseRule {

	public Tag() {
		this.ruleName = "Tag";
		this.__debug__ = true;
	}


	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		// --------------------------------------------------------- detect rule

		if (!this.detectRule()) {
			this.rewind();
			return Node.UNDEFINED;
		}

		// ---------------------------------------------------- tokenize: tag id

		while (this.current != ' ' && this.current != '>' && !this.isEOF()) {
			this.read();
		}

		this.addToken(
			TokensStore.TAG,
			0,
			this.buffer.lastIndex()
		);

		// ----------------------------------------------- tokenize: sub content

		int[] rulesTypes = {
			RulesStore.STATEMENT,
			RulesStore.EXPRESSION,
			RulesStore.STRING_COMPLEX,
			RulesStore.TAG_ATTRIBUTE
		};

		while (!this.isEOF()) {
			SpecificRuleBasedScanner subscanner = this.createScanner(
				TokensStore.DEFAULT,
				rulesTypes
			);

			Node nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;

		// -------------------------------------------- alternative: sub content
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
			} else {
		// ------------------------------------ alternative: unknown sub content
				if (this.current != '>' && this.current != '/') {
					this.addToken(
						TokensStore.DEFAULT,
						1
					);
				} else {
		// -------------------------------------------- alternative: end of rule
					this.addToken(
						TokensStore.TAG,
						1
					);

					if (this.current == '>') {
						return this.containerToken; // ------------------ return
					}
				}
			}

			this.read();
		}

		// --------------------------------------------------------- end of rule

		return this.containerToken;
	}


	private boolean detectRule() {
		this.read();

		if (this.current != '<' || this.isEOF()) {
			return false;
		}

		return true;
	}

}

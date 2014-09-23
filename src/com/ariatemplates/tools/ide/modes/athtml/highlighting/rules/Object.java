package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Object extends BaseRule {

	private static final int LOOKING_FOR_KEY = 0;
	private static final int LOOKING_FOR_VALUE = 1;
	private static final int LOOKING_FOR_NOTHING = 2;

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		// --------------------------------------------------------- detect rule

		this.read();
		if (this.current != '{' || this.isEOF()) {
			this.rewind();
			return Node.UNDEFINED;
		}

		this.addToken(
			TokensStore.OBJECT,
			1
		);

		// ----------------------------------------------- tokenize: sub content

		boolean isObjectOver = false;
		int status = Object.LOOKING_FOR_KEY;

		List<IRule> valueRules = this.rulesStore.getPrimitiveRules();
		int[] keyRulesTypes = {
			RulesStore.KEY
		};
		List<IRule> keyRules = this.rulesStore.getRules(keyRulesTypes);

		int tokenizedLentgh = 0;
		Node nextToken = null;

		while (!this.isEOF() && !isObjectOver) {
			this.read();
			SpecificRuleBasedScanner subscanner = null;

		// ------------------------------------------ choice: sub content parser

			List<IRule> rules = null;

			switch (status) {
				case Object.LOOKING_FOR_KEY:
					rules = keyRules;
					break;
				case Object.LOOKING_FOR_VALUE:
					rules = valueRules;
					break;
			}

			if (rules != null) {
				subscanner = this.createScanner(
					TokensStore.DEFAULT,
					rules
				);

				nextToken = subscanner.getToken(true);
				tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			} else {
				tokenizedLentgh = 0;
			}

		// -------------------------------------------- alternative: sub content
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);

				this.read(tokenizedLentgh - 1);

				if (status == Object.LOOKING_FOR_KEY) {
					status = Object.LOOKING_FOR_NOTHING;
				}
			} else {
				if (this.current == ':') {
					status = Object.LOOKING_FOR_VALUE;
				}
				if (this.current == ',') {
					status = Object.LOOKING_FOR_KEY;
				}

				if (this.current == '}') {
		// -------------------------------------------- alternative: end of rule
					isObjectOver = true;
				}

				this.addToken(
					TokensStore.OBJECT,
					1
				);
			}
		}

		// --------------------------------------------------------- end of rule

		if (this.isEOF()) {
			this.unread();
		}

		return this.containerToken;
	}

}

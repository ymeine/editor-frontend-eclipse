package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
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

	private int[] valueRulesTypes;
	private int[] keyRulesTypes = {
		RulesStore.KEY
	};

	private int state;

	public Object() {
		this.ruleName = "Object";
		this.__debug__ = true;

		this.valueRulesTypes = this.rulesStore.getPrimitiveRulesTypes();

		this.state = -1;
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

		this.addToken(TokensStore.OBJECT, 1);

		// ----------------------------------------------- tokenize: sub content

		this.state = Object.LOOKING_FOR_KEY;

		boolean isRuleOver;
		do {
			isRuleOver = this.continueParsing();
		} while (!this.isEOF() && !isRuleOver);

		// --------------------------------------------------------- end of rule

		if (this.isEOF()) {
			this.unread();
		}

		return this.containerToken;
	}

	protected boolean detectRule() {
		if (this.current != '{') {
			return false;
		}

		return true;
	}

	protected boolean continueParsing() {
		boolean isRuleOver = false;

		this.read();

		int tokenizedLentgh = 0;
		Node nextToken = null;

		// ------------------------------------------ choice: sub content parser

		int[] rules = null;

		switch (this.state) {
			case Object.LOOKING_FOR_KEY:
				rules = this.keyRulesTypes;
				break;
			case Object.LOOKING_FOR_VALUE:
				rules = this.valueRulesTypes;
				break;
		}

		if (rules != null) {
			SpecificRuleBasedScanner subscanner = this.createScanner(
				TokensStore.DEFAULT,
				rules
			);

			nextToken = subscanner.getToken(true);
			tokenizedLentgh = subscanner.getTokenizedLength() - 1;
		} else {
			tokenizedLentgh = 0;
		}

	// ------------------------------------------------ alternative: sub content
		if (tokenizedLentgh > 0) {
			this.addToken(nextToken);

			this.read(tokenizedLentgh - 1);

			if (this.state == Object.LOOKING_FOR_KEY) {
				this.state = Object.LOOKING_FOR_NOTHING;
			}
		} else {
			if (this.current == ':') {
				this.state = Object.LOOKING_FOR_VALUE;
			}
			if (this.current == ',') {
				this.state = Object.LOOKING_FOR_KEY;
			}

			if (this.current == '}') {
	// ------------------------------------------------ alternative: end of rule
				isRuleOver = true;
			}

			this.addToken(TokensStore.OBJECT, 1);
		}

		return isRuleOver;
	}
}

package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Key extends BaseRule {

	private static final String FORBIDDEN_CHARS = " :,\r\t\n{}";



	private int stringDelimiter;

	public Key() {
		this.ruleName = "Key";
		this.__debug__ = true;
		
		this.stringDelimiter = -1;
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

		// --------------------------------------------- alternative: quoted key

		if (this.current == '"' || this.current == '\'') {
			this.stringDelimiter = this.current;

			this.addToken(TokensStore.KEY, 1);

			int previous = this.current;
			this.read();

			while (this.current != this.stringDelimiter || previous == '\\') {
				if (this.__debug__) {
					System.out.println("rule " + this.ruleName + " in loop 1");
				}
				
				if (this.isEOF()) {
					this.rewind();
					return Node.UNDEFINED;
				}

				this.addToken(TokensStore.KEY, 1);

				previous = this.current;
				this.read();
			}
			this.addToken(TokensStore.KEY, 1);

			return this.containerToken;
		}

		// ------------------------------------------- alternative: unquoted key

		while (!this.isEOF() && Key.FORBIDDEN_CHARS.indexOf(this.current) == -1) {
			if (this.__debug__) {
				System.out.println("rule " + this.ruleName + " in loop 2");
			}

			this.addToken(TokensStore.KEY, 1);

			this.read();
		}

		// --------------------------------------------------------- end of rule

		this.unread();

		return this.containerToken;
	}

	protected boolean detectRule() {
		if (Key.FORBIDDEN_CHARS.indexOf(this.current) != -1) {
			return false;
		}

		return true;
	}
}

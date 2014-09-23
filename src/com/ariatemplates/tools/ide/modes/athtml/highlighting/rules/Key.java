package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Key extends BaseRule {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		int stringDelimiter;
		int previous = ' ';

		this.read();

		String forbiddenChars = " :,\r\t\n{}";

		if (this.isEOF() || forbiddenChars.indexOf(this.current) != -1) {
			this.rewind();
			return Node.UNDEFINED;
		}

		if (this.current == '"' || this.current == '\'') {
			stringDelimiter = this.current;

			this.addToken(
				TokensStore.KEY,
				1
			);

			previous = this.current;
			this.read();

			while (this.current != stringDelimiter || previous == '\\') {
				if (this.isEOF()) {
					this.rewind();
					return Node.UNDEFINED;
				}

				this.addToken(
					TokensStore.KEY,
					1
				);

				previous = this.current;
				this.read();
			}
			this.addToken(
				TokensStore.KEY,
				1
			);

			return this.containerToken;
		}

		while (this.isEOF() || forbiddenChars.indexOf(this.current) == -1) {
			this.addToken(
				TokensStore.KEY,
				1
			);

			this.read();
		}

		this.unread();

		return this.containerToken;
	}

}

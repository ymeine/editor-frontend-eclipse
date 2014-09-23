package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Function extends BaseRule {

	private static final String KEYWORD = "function";
	private static final int KEYWORD_LENGTH = KEYWORD.length();

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		// --------------------------------------------------------- detect rule

		this.read(Function.KEYWORD_LENGTH);
		if (this.buffer.toString().compareTo(Function.KEYWORD) != 0) {
			this.rewind();
			return Node.UNDEFINED;
		}

		this.addToken(
			TokensStore.FUNCTION,
			0,
			Function.KEYWORD_LENGTH
		);

		// ----------------------------------------------- tokenize: sub content

		boolean toContinue = this.parseFunctionArguments();

		if (!toContinue) {
			return this.containerToken;
		}

		this.parseFunctionBody();

		return this.containerToken;
	}

	private boolean parseFunctionArguments() {
		int argsOffset = Function.KEYWORD_LENGTH;

		while (!this.isEOF() && this.current != ')') {
			this.read();

			if (this.current == '(') {
				this.addToken(
					TokensStore.FUNCTION,
					8,
					this.offset - 7
				);

				argsOffset = this.offset + 1;
			}
		}

		if (this.isEOF()) {
			this.unread();

			if (argsOffset < this.offset) {
				this.addToken(
					TokensStore.DEFAULT,
					argsOffset,
					this.offset - argsOffset
				);
			}

			return false;
		} else {
			if (argsOffset < this.offset) {
				this.addToken(
					TokensStore.DEFAULT,
					argsOffset,
					this.offset - argsOffset
				);
			}

			this.addToken(
				TokensStore.FUNCTION,
				1
			);
		}

		return true;
	}

	private void parseFunctionBody() {
		int initialOffset = this.offset;
		int bracketsCount = 0;

		while (!this.isEOF() && this.current != '{') {
			this.read();
		}

		if (this.isEOF()) {
			this.unread();

			this.addToken(
				TokensStore.FUNCTION,
				initialOffset,
				this.offset - initialOffset
			);

			return;
		} else {
			this.addToken(
				TokensStore.FUNCTION,
				initialOffset,
				this.offset - initialOffset
			);

			bracketsCount++;
		}

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
						TokensStore.FUNCTION,
						1
					);
				} else {
					this.addToken(
						TokensStore.DEFAULT,
						1
					);
				}
			}
		}
		if (this.current == ICharacterScanner.EOF) {
			this.unread();
		}
	}

}

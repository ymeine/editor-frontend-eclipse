package com.ariatemplates.tools.ide.modes.athtml.highlighting;



import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



/**
 * This class is used as a base class for all those rules whose output is a
 * complex token that just contains a nested structure of tokens. It provides
 * some methods that facilitate the creation of the container token.
 */
public class BaseRule implements IRule {

	protected TokensStore tokenStore = TokensStore.get();
	protected RulesStore rulesStore = RulesStore.get();

	protected int start = 0;
	protected int offset = -1;

	protected SpecificRuleBasedScanner scanner = null;
	protected Node containerToken = null;

	protected Buffer buffer;
	protected int current;

	protected String ruleName = "BaseRule";
	protected boolean __debug__ = false;
	
	
	/**
	 * Sets some properties that are useful when reading a scanner
	 *
	 * @param initialScanner
	 */
	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		if (this.__debug__) {
			System.out.println("parsing: " + this.ruleName);
		}

		this.scanner = (SpecificRuleBasedScanner) initialScanner;
		this.containerToken = this.tokenStore.getToken(TokensStore.CONTAINER);

		this.start = scanner.getCurrentOffset();
		this.offset = -1;

		this.buffer = new Buffer();

		return null;
	}

	protected int read() {
		int current = this.scanner.read();
		this.current = current;

		this.offset++;
		return this.buffer.add(current);
	}

	protected void read(int count) {
		for (int i = 0; i < count; i++) {
			this.read();
		}
	}



	protected void unread() {
		this.scanner.unread();
		this.offset--;
		this.buffer.pop();
	}

	protected void rewind() {
		while (this.offset > -1) {
			this.offset--;
			this.scanner.unread();
		}
		this.buffer.clear();
	}



	protected boolean isEOF() {
		return this.current == ICharacterScanner.EOF;
	}



	protected void addToken(Node token) {
		this.containerToken.addChild(token);
	}

	protected void addToken(String type) {
		this.addToken(this.tokenStore.getToken(type));
	}

	protected void addToken(String type, int offset, int length) {
		this.addToken(this.tokenStore.getToken(
			type,
			this.start + offset,
			length
		));
	}

	protected void addToken(String type, int length) {
		this.addToken(this.tokenStore.getToken(
			type,
			this.start + this.offset,
			length
		));
	}



	protected void removeLastToken() {
		this.containerToken.popChild();
	}



	protected SpecificRuleBasedScanner createScanner(String defaultToken, int[] rulesTypes) {
		return new SpecificRuleBasedScanner(
			defaultToken,
			rulesTypes,
			this.scanner.getDocument(),
			this.start + this.offset
		);
	}

	protected SpecificRuleBasedScanner createScanner(String defaultToken, List<IRule> rules) {
		return new SpecificRuleBasedScanner(
			defaultToken,
			rules,
			this.scanner.getDocument(),
			this.start + this.offset
		);
	}
}

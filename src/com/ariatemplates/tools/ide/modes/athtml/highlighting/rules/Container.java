package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich;



/**
 * This class is used as a base class for all those rules whose output is a
 * complex token that just contains a nested structure of tokens. It provides
 * some methods that facilitate the creation of the container token.
 *
 * @author flongo
 *
 */
public class Container implements IRule {

	protected TokensStore tokenStore = TokensStore.get();

	protected int start = 0;
	protected SpecificRuleBasedScanner scanner = null;
	protected String buffer = "";
	protected List<Integer> intBuffer = new ArrayList<Integer>();
	protected int offset = -1;
	protected Rich containerToken = null;

	/**
	 * Sets some properties that are useful when reading a scanner
	 *
	 * @param initialScanner
	 */
	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		this.scanner = (SpecificRuleBasedScanner) initialScanner;
		this.start = scanner.getCurrentOffset();
		this.buffer = "";
		this.intBuffer.clear();
		this.offset = -1;
		this.containerToken = this.tokenStore.getToken(TokensStore.CONTAINER);
		return null;
	}

	protected int read() {
		int next = this.scanner.read();
		this.offset++;
		this.buffer += (char) next;
		this.intBuffer.add(next);
		return next;
	}

	protected void unread() {
		this.scanner.unread();
		this.offset--;
		this.buffer = this.buffer.substring(0, this.buffer.length() - 1);
		this.intBuffer.remove(this.intBuffer.size() - 1);
	}

	protected void read(int count) {
		for (int i = 0; i < count; i++) {
			this.read();
		}
	}

	protected void rewind() {
		while (this.offset > -1) {
			this.offset--;
			this.scanner.unread();
		}
		this.buffer = "";
		this.intBuffer.clear();
	}

	protected void addToken(Rich token) {
		this.containerToken.addChild(token);
	}

	protected void removeLastToken() {
		this.containerToken.popChild();
	}

}

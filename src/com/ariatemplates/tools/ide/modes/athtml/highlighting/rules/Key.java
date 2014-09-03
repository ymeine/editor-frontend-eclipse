package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich;



public class Key extends Container {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);
		int stringDelimiter;
		char previousChar = ' ';

		int next = this.read();
		char nextChar = (char) next;

		String forbiddenChars = " :,\r\t\n{}";
		if (next == ICharacterScanner.EOF || forbiddenChars.indexOf(nextChar) != -1) {
			this.rewind();
			return Rich.UNDEFINED;
		}

		if (nextChar == '"' || nextChar == '\'') {

			stringDelimiter = next;
			this.addToken(TokensStore.get().getToken(TokensStore.KEY, this.start + this.offset, 1));
			previousChar = nextChar;
			next = this.read();
			nextChar = (char) next;
			while (next != stringDelimiter || (next == stringDelimiter && previousChar == '\\')) {
				if (next == ICharacterScanner.EOF) {
					this.rewind();
					return Rich.UNDEFINED;
				}
				this.addToken(TokensStore.get().getToken(TokensStore.KEY, this.start + this.offset, 1));
				previousChar = nextChar;
				next = this.read();
				nextChar = (char) next;
			}
			this.addToken(TokensStore.get().getToken(TokensStore.KEY, this.start + this.offset, 1));
			return this.containerToken;
		}

		while (next == ICharacterScanner.EOF || forbiddenChars.indexOf(nextChar) == -1) {
			this.addToken(TokensStore.get().getToken(TokensStore.KEY, this.start + this.offset, 1));
			next = this.read();
			nextChar = (char) next;
		}

		this.unread();

		return this.containerToken;
	}

}

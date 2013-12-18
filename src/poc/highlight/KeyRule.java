package poc.highlight;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

public class KeyRule extends ContainerRule {

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
			return RichToken.UNDEFINED;
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
					return RichToken.UNDEFINED;
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

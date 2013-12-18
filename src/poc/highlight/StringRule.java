package poc.highlight;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

public class StringRule extends ContainerRule {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);
		int stringDelimiter;
		char previousChar = ' ';

		int next = this.read();
		char nextChar = (char) next;
		if ((nextChar != '"' && nextChar != '\'') || next == ICharacterScanner.EOF) {
			this.rewind();
			return RichToken.UNDEFINED;
		}
		stringDelimiter = next;
		int[] rulesTypes = { RulesStore.STATEMENT, RulesStore.EXPRESSION };

		this.addToken(TokensStore.get().getToken(TokensStore.STRING, this.start + this.offset, 1));

		previousChar = nextChar;
		next = this.read();
		nextChar = (char) next;
		while (next != stringDelimiter || (next == stringDelimiter && previousChar == '\\')) {
			SpecificRuleBasedScanner subscanner = new SpecificRuleBasedScanner(TokensStore.STRING, rulesTypes, this.scanner.getDocument(), this.start + this.offset);
			RichToken nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
				previousChar = ' ';
			} else {
				if (next == ICharacterScanner.EOF) {
					this.rewind();
					return RichToken.UNDEFINED;
				}
				this.addToken(TokensStore.get().getToken(TokensStore.STRING, this.start + this.offset, 1));
			}
			previousChar = nextChar;
			next = this.read();
			nextChar = (char) next;
		}
		this.addToken(TokensStore.get().getToken(TokensStore.STRING, this.start + this.offset, 1));

		return this.containerToken;
	}
}

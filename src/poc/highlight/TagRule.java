package poc.highlight;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

public class TagRule extends ContainerRule {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		int next = this.read();
		char nextChar = (char) next;
		if (nextChar != '<' || next == ICharacterScanner.EOF) {
			this.rewind();
			return RichToken.UNDEFINED;
		}
		while (nextChar != ' ' && nextChar != '>' && next != ICharacterScanner.EOF) {
			next = this.read();
			nextChar = (char) next;
		}

		this.addToken(TokensStore.get().getToken(TokensStore.TAG, this.start, this.buffer.length() - 1));

		int[] rulesTypes = { RulesStore.STATEMENT, RulesStore.EXPRESSION, RulesStore.STRING_COMPLEX, RulesStore.TAG_ATTRIBUTE };

		while (next != ICharacterScanner.EOF) {
			SpecificRuleBasedScanner subscanner = new SpecificRuleBasedScanner(TokensStore.DEFAULT, rulesTypes, this.scanner.getDocument(), this.start + this.offset);
			RichToken nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
			} else {
				if (nextChar == '>' || nextChar == '/') {
					this.addToken(TokensStore.get().getToken(TokensStore.TAG, this.start + this.offset, 1));
					if (nextChar == '>') {
						return this.containerToken;
					}
				} else {
					this.addToken(TokensStore.get().getToken(TokensStore.DEFAULT, this.start + this.offset, 1));
				}
			}
			next = this.read();
			nextChar = (char) next;
		}

		return this.containerToken;
	}

}

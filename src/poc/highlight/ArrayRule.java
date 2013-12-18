package poc.highlight;

import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

public class ArrayRule extends ContainerRule {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		int next = this.read();
		char nextChar = (char) next;
		if (nextChar != '[' || next == ICharacterScanner.EOF) {
			this.rewind();
			return RichToken.UNDEFINED;
		}
		this.addToken(this.tokenStore.getToken(TokensStore.ARRAY, this.start + this.offset, 1));

		next = 0;
		boolean isArrayOver = false;

		List<IRule> valueRules = RulesStore.get().getPrimitiveRules();
		int tokenizedLentgh = 0;
		RichToken nextToken = null;

		while (next != ICharacterScanner.EOF && !isArrayOver) {
			next = this.read();
			nextChar = (char) next;
			SpecificRuleBasedScanner subscanner = null;
			subscanner = new SpecificRuleBasedScanner(TokensStore.DEFAULT, valueRules, this.scanner.getDocument(), this.start + this.offset);
			nextToken = subscanner.getToken(true);
			tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
			} else {
				if (nextChar == ']') {
					isArrayOver = true;
				}
				this.addToken(this.tokenStore.getToken(TokensStore.ARRAY, this.start + this.offset, 1));
			}
		}
		if (next == ICharacterScanner.EOF) {
			this.unread();
		}

		return this.containerToken;
	}

}

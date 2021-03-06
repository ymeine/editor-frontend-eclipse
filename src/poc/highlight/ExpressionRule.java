package poc.highlight;

import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

public class ExpressionRule extends ContainerRule {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);
		int bracketsCount = 0;

		int next = this.read();
		char nextChar = (char) next;
		if (nextChar != '$' || next == ICharacterScanner.EOF) {
			this.rewind();
			return RichToken.UNDEFINED;
		}
		next = this.read();
		nextChar = (char) next;
		if (nextChar != '{' || next == ICharacterScanner.EOF) {
			this.rewind();
			return RichToken.UNDEFINED;
		}
		bracketsCount++;
		this.addToken(this.tokenStore.getToken(TokensStore.EXPRESSION, this.start, 2));


		next = 0;
		List<IRule> rules = RulesStore.get().getPrimitiveRules();
		while (next != ICharacterScanner.EOF && bracketsCount > 0) {
			next = this.read();
			nextChar = (char) next;
			SpecificRuleBasedScanner subscanner = new SpecificRuleBasedScanner(TokensStore.DEFAULT, rules, this.scanner.getDocument(), this.start + this.offset);
			RichToken nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
			} else {
				if (nextChar == '{') {
					bracketsCount++;
				}
				if (nextChar == '}') {
					bracketsCount--;
				}
				if (bracketsCount == 0) {
					this.addToken(this.tokenStore.getToken(TokensStore.EXPRESSION, this.start + this.offset, 1));
				} else if (next != ICharacterScanner.EOF) {
					this.addToken(this.tokenStore.getToken(TokensStore.EXPRESSION_ARGS, this.start + this.offset, 1));
				}
			}
		}
		if (next == ICharacterScanner.EOF) {
			this.unread();
		}

		return this.containerToken;

	}
}

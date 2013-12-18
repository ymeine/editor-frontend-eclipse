package poc.highlight;

import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

public class ObjectRule extends ContainerRule {

	private static int LOOKING_FOR_KEY = 0;
	private static int LOOKING_FOR_VALUE = 1;
	private static int LOOKING_FOR_NOTHING = 2;

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		int next = this.read();
		char nextChar = (char) next;
		if (nextChar != '{' || next == ICharacterScanner.EOF) {
			this.rewind();
			return RichToken.UNDEFINED;
		}
		this.addToken(this.tokenStore.getToken(TokensStore.OBJECT, this.start + this.offset, 1));

		next = 0;
		boolean isObjectOver = false;
		int status = ObjectRule.LOOKING_FOR_KEY;

		List<IRule> valueRules = RulesStore.get().getPrimitiveRules();
		int[] keyRulesTypes = { RulesStore.KEY };
		List<IRule> keyRules = RulesStore.get().getRules(keyRulesTypes);
		int tokenizedLentgh = 0;
		RichToken nextToken = null;

		while (next != ICharacterScanner.EOF && !isObjectOver) {
			next = this.read();
			nextChar = (char) next;
			SpecificRuleBasedScanner subscanner = null;
			if (status == ObjectRule.LOOKING_FOR_KEY) {
				subscanner = new SpecificRuleBasedScanner(TokensStore.DEFAULT, keyRules, this.scanner.getDocument(), this.start + this.offset);
				nextToken = subscanner.getToken(true);
				tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			} else if (status == ObjectRule.LOOKING_FOR_VALUE) {
				subscanner = new SpecificRuleBasedScanner(TokensStore.DEFAULT, valueRules, this.scanner.getDocument(), this.start + this.offset);
				nextToken = subscanner.getToken(true);
				tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			} else {
				tokenizedLentgh = 0;
			}
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
				if (status == ObjectRule.LOOKING_FOR_KEY) {
					status = ObjectRule.LOOKING_FOR_NOTHING;
				}
			} else {
				if (nextChar == ':') {
					status = ObjectRule.LOOKING_FOR_VALUE;
				}
				if (nextChar == ',') {
					status = ObjectRule.LOOKING_FOR_KEY;
				}
				if (nextChar == '}') {
					isObjectOver = true;
				}
				this.addToken(this.tokenStore.getToken(TokensStore.OBJECT, this.start + this.offset, 1));
			}
		}
		if (next == ICharacterScanner.EOF) {
			this.unread();
		}

		return this.containerToken;

	}

}

package poc.highlight;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

public class AttributeRule extends ContainerRule {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);
		int next = this.read();
		char charNext = (char) next;
		if (this.buffer.matches("[\\w-_:\\s=]")) {
			if (charNext == '=') {
				return TokensStore.get().getToken(TokensStore.TAG_ATTRIBUTE_EQUAL, this.start + this.offset, 1);
			} else {
				return TokensStore.get().getToken(TokensStore.TAG_ATTRIBUTE, this.start + this.offset, 1);
			}
		}
		this.rewind();
		return RichToken.UNDEFINED;
	}

}

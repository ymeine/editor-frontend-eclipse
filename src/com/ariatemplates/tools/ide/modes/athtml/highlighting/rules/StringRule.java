package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class StringRule extends Container {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);
		
		
		
		if (!this.detectRule()) {
			this.rewind();
			return Node.UNDEFINED;
		}
		this.addToken(this.tokenStore.getToken(
			TokensStore.STRING,
			this.start + this.offset,
			1
		));
		
		
		
		char lastCharacter = this.buffer.charAt(this.buffer.length() - 1);
		char stringDelimiter = lastCharacter;
		char previousChar = lastCharacter;
		
		char next = (char) this.read();
		while (!(next == stringDelimiter && previousChar != '\\')) {
			int[] rulesTypes = {
				RulesStore.STATEMENT,
				RulesStore.EXPRESSION
			};
	
			SpecificRuleBasedScanner subscanner = new SpecificRuleBasedScanner(
				TokensStore.STRING,
				rulesTypes,
				this.scanner.getDocument(),
				this.start + this.offset
			);
			
			Node nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);
				
				this.read(tokenizedLentgh - 1);
				previousChar = ' ';
			} else {
				if (next == ICharacterScanner.EOF) {
					this.rewind();
					return Node.UNDEFINED;
				}
				
				this.addToken(this.tokenStore.getToken(
					TokensStore.STRING,
					this.start + this.offset,
					1
				));
			}
			
			previousChar = next;
			next = (char) this.read();
		}
		
		this.addToken(this.tokenStore.getToken(
			TokensStore.STRING,
			this.start + this.offset,
			1
		));

		return this.containerToken;
	}
	
	protected boolean detectRule() {
		char next = (char) this.read();
		
		if ((next != '"' && next != '\'') || next == ICharacterScanner.EOF) {
			return false;
		}
		
		return true;
	}
}

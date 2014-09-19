package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich;



public class Function extends Container {

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		this.read(8);
		if (this.buffer.compareTo("function") != 0) {
			this.rewind();
			return Rich.UNDEFINED;
		}

		this.addToken(this.tokenStore.getToken(
			TokensStore.FUNCTION,
			this.start,
			8
		));

		boolean toContinue = this.parseFunctionArguments();
		if (!toContinue) {
			return this.containerToken;
		}

		this.parseFunctionBody();
		return this.containerToken;

	}

	private boolean parseFunctionArguments() {
		int argsOffset = 8;
		
		int next = 0;
		char charNext = ' ';
		
		while (next != ICharacterScanner.EOF && charNext != ')') {
			next = this.read();
			charNext = (char) next;
			
			if (charNext == '(') {
				this.addToken(this.tokenStore.getToken(
					TokensStore.FUNCTION,
					this.start + 8,
					this.offset - 7
				));
				
				argsOffset = this.offset + 1;
			}
		}
		if (next == ICharacterScanner.EOF) {
			this.unread();
			
			if (argsOffset < this.offset) {
				this.addToken(this.tokenStore.getToken(
					TokensStore.DEFAULT,
					this.start + argsOffset,
					this.offset - argsOffset
				));
			}
			
			return false;
		} else {
			if (argsOffset < this.offset) {
				this.addToken(this.tokenStore.getToken(
					TokensStore.DEFAULT,
					this.start + argsOffset,
					this.offset - argsOffset
				));
			}
			this.addToken(this.tokenStore.getToken(
				TokensStore.FUNCTION,
				this.start + this.offset, 1
			));
		}
		return true;
	}

	private void parseFunctionBody() {
		int initialOffset = this.offset;
		int bracketsCount = 0;
		
		int next = 0;
		char charNext = ' ';

		while (next != ICharacterScanner.EOF && charNext != '{') {
			next = this.read();
			charNext = (char) next;
		}
		if (next == ICharacterScanner.EOF) {
			this.unread();
			
			this.addToken(this.tokenStore.getToken(
				TokensStore.FUNCTION,
				initialOffset,
				this.offset - initialOffset
			));
			
			return;
		} else {
			this.addToken(this.tokenStore.getToken(
				TokensStore.FUNCTION,
				initialOffset,
				this.offset - initialOffset
			));
			
			bracketsCount++;
		}
		
		List<IRule> rules = RulesStore.get().getPrimitiveRules();
		
		while (next != ICharacterScanner.EOF && bracketsCount > 0) {
			next = this.read();
			charNext = (char) next;
			
			SpecificRuleBasedScanner subscanner = new SpecificRuleBasedScanner(
				TokensStore.DEFAULT,
				rules,
				this.scanner.getDocument(),
				this.start + this.offset
			);
			Rich nextToken = subscanner.getToken(true);
			int tokenizedLentgh = subscanner.getTokenizedLength() - 1;
			
			if (tokenizedLentgh > 0) {
				this.addToken(nextToken);
				this.read(tokenizedLentgh - 1);
			} else {
				if (charNext == '{') {
					bracketsCount++;
				}
				if (charNext == '}') {
					bracketsCount--;
				}
				
				if (bracketsCount == 0) {
					this.addToken(this.tokenStore.getToken(
						TokensStore.FUNCTION,
						this.start + this.offset,
						1
					));
				} else {
					this.addToken(this.tokenStore.getToken(
						TokensStore.DEFAULT,
						this.start + this.offset,
						1
					));
				}
			}
		}
		if (next == ICharacterScanner.EOF) {
			this.unread();
		}
	}

}

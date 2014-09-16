package com.ariatemplates.tools.ide.modes.athtml.highlighting;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich;



/**
 * This class provides an implementation of the RuleBasedScanner which supports
 * a hierarchy of nested tokens. It can also be used in order to parse a
 * fragment of the document
 *
 * @author flongo
 *
 */
public class SpecificRuleBasedScanner extends RuleBasedScanner {

	private TokensStore tokenStore = TokensStore.get();
	private RulesStore rulesStore = RulesStore.get();
	private List<Iterator<IToken>> iteratorsStack = new ArrayList<Iterator<IToken>>();
	private IToken currentToken = null;
	private int initialOffset = 0;
	private String defaultTokenType;

	public SpecificRuleBasedScanner() {
		this.setDefaultReturnToken(this.tokenStore.getToken(TokensStore.DEFAULT));
		List<IRule> rules = rulesStore.getRules();
		IRule[] typeArray = new IRule[rules.size()];
		this.setRules(rules.toArray(typeArray));
	}

	public SpecificRuleBasedScanner(String defaultToken, int[] rulesTypes, IDocument document, int offset, int length) {
		this(defaultToken, RulesStore.get().getRules(rulesTypes), document, offset, length);
	}

	public SpecificRuleBasedScanner(String defaultToken, List<IRule> rules, IDocument document, int offset, int length) {
		this.initialOffset = offset;
		this.defaultTokenType = defaultToken;
		this.setDefaultReturnToken(this.tokenStore.getToken(defaultToken));
		IRule[] typeArray = new IRule[rules.size()];
		this.setRules(rules.toArray(typeArray));
		this.setRange(document, offset, length);
	}

	public SpecificRuleBasedScanner(String defaultToken, int[] rulesTypes, IDocument document, int offset) {
		this(defaultToken, rulesTypes, document, offset, document.getLength() - offset);
	}

	public SpecificRuleBasedScanner(String defaultToken, List<IRule> rules, IDocument document, int offset) {
		this(defaultToken, rules, document, offset, document.getLength() - offset);
	}

	/**
	 * It overrides the parent's implementation in order to be able to handle a
	 * tree of tokens
	 *
	 * @return the next token
	 */
	@Override
	public IToken nextToken() {
		IToken temporaryToken = null;
		if (this.iteratorsStack.isEmpty()) {
			temporaryToken = super.nextToken();
		} else {
			Iterator<IToken> lastIterator = this.iteratorsStack.get(this.iteratorsStack.size() - 1);
			if (lastIterator.hasNext()) {
				temporaryToken = lastIterator.next();
			} else {
				this.iteratorsStack.remove(this.iteratorsStack.size() - 1);
				temporaryToken = this.nextToken();
			}
		}

		Rich enhancedToken = (temporaryToken instanceof Rich) ? (Rich) temporaryToken : null;
		if (enhancedToken != null && enhancedToken.hasChildren()) {
			this.iteratorsStack.add(enhancedToken.getChildren().iterator());
			temporaryToken = this.nextToken();
		}

		if (temporaryToken.isUndefined()) {
			return this.nextToken();
		}
		this.currentToken = temporaryToken;
		return temporaryToken;
	}

	/**
	 * It returns the next token independently of its children. It does not
	 * flatten the hierarchy
	 *
	 * @return
	 */
	public IToken nextFlatToken() {
		IToken temporaryToken = null;
		temporaryToken = super.nextToken();
		this.currentToken = temporaryToken;
		return temporaryToken;
	}

	@Override
	public int getTokenOffset() {
		if (this.currentToken == null) {
			return 0;
		}
		int offset = -1;
		if (this.currentToken instanceof Rich) {
			Rich enhancedToken = (Rich) this.currentToken;
			offset = enhancedToken.getOffset();
		}
		if (offset == -1) {
			return super.getTokenOffset();
		}
		return offset;
	}

	@Override
	public int getTokenLength() {
		if (this.currentToken == null) {
			return 0;
		}
		int length = -1;
		if (this.currentToken instanceof Rich) {
			Rich enhancedToken = (Rich) this.currentToken;
			length = enhancedToken.getLength();
		}
		if (length == -1) {
			return super.getTokenLength();
		}
		return length;
	}

	public int getCurrentOffset() {
		return this.fOffset;
	}

	public IDocument getDocument() {
		return this.fDocument;
	}

	/**
	 * It returns the list of token that can be obtained with the given rules.
	 * It can be used as a direct API when the scanner is used programmatically
	 * to parse portions of the document. If the stopAtDefault argument is set
	 * to true, the parser will stop when the default token is found.
	 *
	 * @param stopAtDefault
	 * @return
	 */
	public Rich getToken(boolean stopAtDefault) {
		Rich containerToken = this.tokenStore.getToken(TokensStore.CONTAINER);
		while (this.fOffset < this.fRangeEnd) {
			int initialOffset = this.fOffset;
			IToken next = this.nextFlatToken();
			int finalOffset = this.fOffset;
			Rich pocNext = (Rich) next;
			Rich pocNextClone = pocNext.clone();
			if (pocNextClone.getLength() == Rich.UNDEFINED_INT && pocNextClone.getOffset() == Rich.UNDEFINED_INT) {
				pocNextClone.setOffset(initialOffset);
				pocNextClone.setLength(finalOffset - initialOffset);
			}
			if (stopAtDefault && pocNextClone.getType() == this.defaultTokenType) {
				return containerToken;
			}
			this.currentToken = pocNextClone;
			containerToken.addChild(this.currentToken);
		}
		return containerToken;
	}

	public Rich getToken() {
		return this.getToken(false);
	}

	/**
	 * @return the number of characters that have been tokenized plus the first
	 *         one that has been associated to the default token, in case the
	 *         getToken method is called with a true argument
	 */
	public int getTokenizedLength() {
		return this.getCurrentOffset() - this.initialOffset;
	}

}

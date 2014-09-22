package com.ariatemplates.tools.ide.modes.athtml.highlighting;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.TokensStore;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.RulesStore;



/**
 * This class provides an implementation of the RuleBasedScanner which supports
 * a hierarchy of nested tokens. It can also be used in order to parse a
 * fragment of the document
 *
 * @author flongo
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
		IToken next = null;
		
		if (this.iteratorsStack.isEmpty()) {
			next = super.nextToken();
		} else {
			int lastIndex = this.iteratorsStack.size() - 1;
			Iterator<IToken> lastIterator = this.iteratorsStack.get(lastIndex);
			
			if (lastIterator.hasNext()) {
				next = lastIterator.next();
			} else {
				this.iteratorsStack.remove(lastIndex);
				next = this.nextToken();
			}
		}

		Node enhancedToken = (next instanceof Node) ? (Node) next : null;
		
		if (enhancedToken != null && enhancedToken.hasChildren()) {
			this.iteratorsStack.add(enhancedToken.getChildren().iterator());
			next = this.nextToken();
		}

		if (next.isUndefined()) {
			return this.nextToken();
		}
		
		this.currentToken = next;
		
		return next;
	}

	/**
	 * It returns the next token independently of its children. It does not
	 * flatten the hierarchy
	 *
	 * @return
	 */
	public IToken nextFlatToken() {
		this.currentToken = super.nextToken();
		return this.currentToken;
	}

	@Override
	public int getTokenOffset() {
		if (this.currentToken == null) {
			return 0;
		}
		
		int offset = -1;
		
		if (this.currentToken instanceof Node) {
			offset = ((Node) this.currentToken).getOffset();
		}
		
		if (offset == -1) {
			offset = super.getTokenOffset();
		}
		
		return offset;
	}

	@Override
	public int getTokenLength() {
		if (this.currentToken == null) {
			return 0;
		}
		
		int length = -1;
		
		if (this.currentToken instanceof Node) {
			length = ((Node) this.currentToken).getLength();
		}
		
		if (length == -1) {
			length = super.getTokenLength();
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
	public Node getToken(boolean stopAtDefault) {
		Node containerToken = this.tokenStore.getToken(TokensStore.CONTAINER);
		
		while (this.fOffset < this.fRangeEnd) {
			int initialOffset = this.fOffset;
			Node next = ((Node) this.nextFlatToken()).clone();
			int finalOffset = this.fOffset;
			
			if (next.getLength() == Node.UNDEFINED_INT && next.getOffset() == Node.UNDEFINED_INT) {
				next.setOffset(initialOffset);
				next.setLength(finalOffset - initialOffset);
			}
			
			if (stopAtDefault && next.getType() == this.defaultTokenType) {
				return containerToken;
			}
			
			this.currentToken = next;
			containerToken.addChild(this.currentToken);
		}
		
		return containerToken;
	}

	public Node getToken() {
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

package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.BaseRule;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node.Node;



public class Word extends BaseRule {

	private List<String> words;
	private List<Node> tokens = null;
	private Node defaultToken;
	private int maxLength = 0;

	public Word(String[] words, Node[] tokens) {
		this.words = new ArrayList<String>(Arrays.asList(words));
		this.maxLength = this.longestWordLength(words);
		this.tokens = new ArrayList<Node>(Arrays.asList(tokens));
	}

	public Word(String[] words, Node defaultToken) {
		this.words = new ArrayList<String>(Arrays.asList(words));
		this.maxLength = this.longestWordLength(words);
		this.defaultToken = defaultToken;
	}

	public int longestWordLength(String[] words) {
		List<Integer> lengths = new ArrayList<Integer>(words.length);

		for (String word: words) {
			lengths.add(word.length());
		}

		return Collections.max(lengths);
	}

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		this.read();
		int counter = 1;
		int index = this.words.indexOf(this.buffer);

		while (!this.isEOF() && this.current != '\r' && this.current != '\n' && counter < this.maxLength && index == -1) {
			this.read();
			counter++;
			index = this.words.indexOf(this.buffer);
		}

		if (this.buffer.content.isEmpty()) {
			this.rewind();
			return Node.UNDEFINED;
		}
		if (index != -1) {
			Node returnToken = this.tokens == null ? this.defaultToken.clone() : this.tokens.get(index).clone();

			returnToken.setOffset(this.start);
			returnToken.setLength(this.buffer.content.size());

			return returnToken;
		}

		this.rewind();

		return Node.UNDEFINED;
	}

}

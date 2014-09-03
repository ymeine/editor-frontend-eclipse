package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich;



public class Word extends Container {

	private List<String> words;
	private List<Rich> tokens = null;
	private Rich defaultToken;
	private int maxLength = 0;

	public Word(String[] words, Rich[] tokens) {
		this.words = new ArrayList<String>(Arrays.asList(words));
		this.maxLength = this.longestWordLength(words);
		this.tokens = new ArrayList<Rich>(Arrays.asList(tokens));
	}

	public Word(String[] words, Rich defaultToken) {
		this.words = new ArrayList<String>(Arrays.asList(words));
		this.maxLength = this.longestWordLength(words);
		this.defaultToken = defaultToken;
	}

	public int longestWordLength(String[] words) {
		int maxLength = 0;
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > maxLength) {
				maxLength = words[i].length();
			}
		}
		return maxLength;
	}

	@Override
	public IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate(initialScanner);

		int next = this.read();
		char nextChar = (char) next;
		int counter = 1;
		int index = this.words.indexOf(this.buffer);

		while (next != ICharacterScanner.EOF && nextChar != '\r' && nextChar != '\n' && counter < this.maxLength && index == -1) {
			next = this.read();
			nextChar = (char) next;
			counter++;
			index = this.words.indexOf(this.buffer);
		}

		if (this.buffer.length() == 0) {
			this.rewind();
			return Rich.UNDEFINED;
		}
		if (index != -1) {
			Rich returnToken = this.tokens == null ? this.defaultToken.clone() : this.tokens.get(index).clone();
			returnToken.setOffset(this.start);
			returnToken.setLength(this.buffer.length());
			return returnToken;
		}
		this.rewind();
		return Rich.UNDEFINED;
	}
}

package com.ariatemplates.tools.ide.modes.athtml.highlighting.rules



import org.eclipse.jface.text.rules.ICharacterScanner
import org.eclipse.jface.text.rules.IToken

import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



class Word extends Container {

	private words
	private tokens
	private defaultToken
	private maxLength = 0



	Word(words, tokens) {
		this.words = words
		this.maxLength = Collections.max(words*.length())

		if (tokens instanceof Rich) {
			this.defaultToken = tokens
		} else {
			this.tokens = tokens
		}
	}



	IToken evaluate(ICharacterScanner initialScanner) {
		super.evaluate initialScanner

		def next = this.read()
		def counter = 1
		def index = this.words.indexOf this.buffer

		while (
			next != ICharacterScanner.EOF
			&& next != '\r'
			&& next != '\n'
			&& counter < this.maxLength
			&& index == -1
		) {
			next = this.read()
			counter++
			index = this.words.indexOf this.buffer
		}

		if (this.buffer.length() == 0) {
			this.rewind()
			return Rich.UNDEFINED
		}

		if (index != -1) {
			// def returnToken = this.tokens == null ? this.defaultToken.clone() : this.tokens.get(index).clone()
			def returnToken = this.tokens?.get(index).clone() ?: this.defaultToken.clone()

			returnToken.offset = this.start
			returnToken.length = this.buffer.length()

			return returnToken
		}

		this.rewind()

		Rich.UNDEFINED
	}
}

package com.ariatemplates.tools.ide.editor.highlighting.tokens.scanner



import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.TextAttribute
import org.eclipse.jface.text.rules.IToken
import org.eclipse.jface.text.rules.ITokenScanner
import org.eclipse.jface.text.rules.Token
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.widgets.Display

import com.ariatemplates.tools.ide.backend.backend.Backend
import com.ariatemplates.tools.ide.document.document.Document



class Scanner implements ITokenScanner {

	// Tokens ------------------------------------------------------------------

	private tokensIterator
	private currentToken

	// Styles ------------------------------------------------------------------

	private defaultStyle
	private styles



	Scanner() {}



	/***************************************************************************
	 * Data update
	 **************************************************************************/

	private static final ARGUMENT_OFFSET = "offset"

	private static final METHOD_HIGHLIGHT = "highlight"
	private static final KEY_TOKENS = "ranges"

	void setRange(IDocument document, int offset, int length) {
		try {
			def cl = this.class
			// Styles ----------------------------------------------------------

			//this.getStylesheet mode
			def stylesheet = Backend.get().service document, cl.METHOD_STYLESHEET
			this.defaultStyle = stylesheet[cl.KEY_DEFAULT_STYLE]
			this.styles = stylesheet[cl.KEY_STYLES]

			// Tokens ----------------------------------------------------------

			def result = Backend.get().service(document, cl.METHOD_HIGHLIGHT, [
				"wholeSource": true,
				(cl.ARGUMENT_OFFSET): offset,
				"end": offset + length
			])

			tokens = result[cl.KEY_TOKENS]

			this.tokensIterator = tokens.iterator()
		} catch (e) {
			e.printStackTrace()
		}
	}



	/***************************************************************************
	 * Stylesheet update
	 **************************************************************************/

	// Constants ---------------------------------------------------------------

	private static final METHOD_STYLESHEET = "stylesheet"
	private static final KEY_DEFAULT_STYLE = "default"
	private static final KEY_STYLES = "styles"

	// Cache -------------------------------------------------------------------

	// private static safeCache = true
	// private stylesheets = [:]

	// // TODO Create or find a generic cache utility. Integrate this cache utility in the Backend class? It would make sense!! This is not optmize communication with the backend, which should implement a generic cache system
	// private void getStylesheet(document) {
	// 	def stylesheet
	// 	def mode = this.class.mode
	// 	def METHOD_STYLESHEET = this.class.METHOD_STYLESHEET

	// 	// Cache ---------------------------------------------------------------

	// 	if (this.stylesheets.containsKey(mode)) {
	// 		if (safeCache) {
	// 			try {
	// 				def response = Backend.get().editor "mode", METHOD_STYLESHEET, [
	// 					"checkCache": true,
	// 					"sendIfObsolete": true
	// 				]

	// 				if (response["obsolete"]) {
	// 					stylesheet = response["stylesheet"]
	// 					this.stylesheets[mode] = stylesheet
	// 				} else {
	// 					stylesheet = this.stylesheets[mode]
	// 				}
	// 			} catch (JsonSyntaxException | ParseException | BackendException e) {
	// 				e.printStackTrace()
	// 			}

	// 		} else {
	// 			stylesheet = stylesheets[mode]
	// 		}
	// 	} else {
	// 		try {
	// 			stylesheet = Backend.get().service(document, METHOD_STYLESHEET)["stylesheet"]
	// 			this.stylesheets[mode] = stylesheet
	// 		} catch (JsonSyntaxException | ParseException | BackendException e) {
	// 			e.printStackTrace()
	// 		}
	// 	}

	// 	// Stylesheet extractions ----------------------------------------------

	// 	this.defaultStyle = stylesheet[this.class.KEY_DEFAULT_STYLE]
	// 	this.styles = stylesheet[this.class.KEY_STYLES]
	// }



	/***************************************************************************
	 * Tokens management
	 **************************************************************************/

	private static final KEY_STYLE = "style"

	IToken nextToken() {
		if (this.tokensIterator == null || !this.tokensIterator.hasNext()) {
			return Token.EOF
		}

		this.currentToken = ++this.tokensIterator
		def type = this.currentToken[this.class.KEY_STYLE]

		if (type == "ws") {
			return Token.WHITESPACE
		}

		new Token(this.getAttribute(type))
	}

	// TODO Caching?
	private getAttribute(type) {
  		def style = this.styles[type] ?: this.defaultStyle
		def rgb = style["color"] ?: this.defaultStyle["color"]

		new TextAttribute(new Color(
			Display.current,
			*('rgb'.collect {k->rgb[k]})
		))


	}

	// Locations ---------------------------------------------------------------

	int getTokenOffset() {
		this.currentToken["start"]
	}

	int getTokenLength() {
		this.currentToken["end"] - this.tokenOffset
	}

}

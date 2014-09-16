package com.ariatemplates.tools.ide.editor.highlighting.tokens.scanner;



import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.ariatemplates.tools.ide.backend.backend.Backend;
import com.ariatemplates.tools.ide.document.document.Document;



public class Scanner implements ITokenScanner {

	// Tokens ------------------------------------------------------------------

	private Iterator<Map<String, Object>> tokensIterator = null;
	private Map<String, Object> currentToken = null;

	// Styles ------------------------------------------------------------------

	private Map<String, Object> defaultStyle = null;
	private Map<String, Object> styles = null;



	public Scanner() {}



	/***************************************************************************
	 * Data update
	 **************************************************************************/

	private static final String ARGUMENT_OFFSET = "offset";

	private static final String METHOD_HIGHLIGHT = "highlight";
	private static final String KEY_TOKENS = "ranges";

	@Override
	@SuppressWarnings("unchecked")
	public void setRange(IDocument document, int offset, int length) {
		Document doc = (Document) document;

		try {

			// Styles ----------------------------------------------------------
			//this.getStylesheet(mode);
			Map<String, Object> stylesheet = (Map<String, Object>) Backend.get().service(doc, Scanner.METHOD_STYLESHEET);
			this.defaultStyle = (Map<String, Object>) stylesheet.get(Scanner.KEY_DEFAULT_STYLE);
			this.styles = (Map<String, Object>) stylesheet.get(Scanner.KEY_STYLES);

			// Tokens ----------------------------------------------------------
			Map<String, Object> argument = new HashMap<String, Object>();
			argument.put("wholeSource", true);
			argument.put(Scanner.ARGUMENT_OFFSET, offset);
			argument.put("end", offset + length);

			Map<String, Object> result = Backend.get().service(doc, Scanner.METHOD_HIGHLIGHT, argument);

			List<Map<String, Object>> tokens = (List<Map<String, Object>>) result.get(Scanner.KEY_TOKENS);

			this.tokensIterator = tokens.iterator();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/***************************************************************************
	 * Stylesheet update
	 **************************************************************************/

	// Constants ---------------------------------------------------------------

	private static final String METHOD_STYLESHEET = "stylesheet";
	private static final String KEY_DEFAULT_STYLE = "default";
	private static final String KEY_STYLES = "styles";

	// Cache -------------------------------------------------------------------

	// private static boolean safeCache = true;
	// private Map<String, Object> stylesheets = new HashMap<String, Object>();

	// // TODO Create or find a generic cache utility. Integrate this cache utility in the Backend class? It would make sense!! This is not optimize communication with the backend, which should implement a generic cache system
	// @SuppressWarnings("unchecked")
	// private void getStylesheet(Document document) throws IOException {
	// 	Map<String, Object> stylesheet = null;

	// 	// Cache ---------------------------------------------------------------

	// 	if (this.stylesheets.containsKey(Scanner.mode)) {
	// 		if (this.safeCache) {
	// 			Map<String, Object> options = new HashMap<String, Object>();
	// 			options.put("checkCache", true);
	// 			options.put("sendIfObsolete", true);

	// 			try {
	// 				Map<String, Object> response = Backend.get().editor("mode", Scanner.METHOD_STYLESHEET, options);
	// 				if ((Boolean) response.get("obsolete")) {
	// 					stylesheet = (Map<String, Object>) response.get(Scanner.KEY_STYLESHEET);
	// 					stylesheets.put(Scanner.mode, stylesheet);
	// 				} else {
	// 					stylesheet = (Map<String, Object>) stylesheets.get(Scanner.mode);
	// 				}
	// 			} catch (JsonSyntaxException | ParseException | BackendException e) {
	// 				e.printStackTrace();
	// 			}

	// 		} else {
	// 			stylesheet = (Map<String, Object>) stylesheets.get(Scanner.mode);
	// 		}
	// 	} else {
	// 		try {
	// 			stylesheet = (Map<String, Object>) Backend.get().service(document, Scanner.METHOD_STYLESHEET).get(Scanner.KEY_STYLESHEET);
	// 			this.stylesheets.put(Scanner.mode, stylesheet);
	// 		} catch (JsonSyntaxException | ParseException | BackendException e) {
	// 			e.printStackTrace();
	// 		}
	// 	}

	// 	// Stylesheet extractions ----------------------------------------------

	// 	this.defaultStyle = (Map<String, Object>) stylesheet.get(Scanner.KEY_DEFAULT_STYLE);
	// 	this.styles = (Map<String, Object>) stylesheet.get(Scanner.KEY_STYLES);
	// }



	/***************************************************************************
	 * Tokens management
	 **************************************************************************/

	private static final String KEY_STYLE = "style";

	private static final String STYLE_WS = "ws";

	@Override
	public IToken nextToken() {
		if (this.tokensIterator == null || !this.tokensIterator.hasNext()) {
			return Token.EOF;
		}

		this.currentToken = this.tokensIterator.next();
		String type = (String) this.currentToken.get(Scanner.KEY_STYLE);

		if (type.equals(STYLE_WS)) {
			return Token.WHITESPACE;
		}

		return new Token(this.getAttribute(type));
	}

	private static final String KEY_COLOR = "color";
	private static final String KEY_RED = "r";
	private static final String KEY_GREEN = "g";
	private static final String KEY_BLUE = "b";

	// TODO Caching?
	@SuppressWarnings("unchecked")
	private TextAttribute getAttribute(String type) {
  		Map<String, Object> style = (Map<String, Object>) this.styles.get(type);
		if (style == null) {
			style = this.defaultStyle;
		}

		Map<String, Object> rgb = (Map<String, Object>) style.get(Scanner.KEY_COLOR);
		if (rgb == null) {
			rgb = (Map<String, Object>) this.defaultStyle.get(Scanner.KEY_COLOR);
		}
		return new TextAttribute(new Color(
			Display.getCurrent(),
			((Number)rgb.get(Scanner.KEY_RED)).intValue(),
			((Number)rgb.get(Scanner.KEY_GREEN)).intValue(),
			((Number)rgb.get(Scanner.KEY_BLUE)).intValue()
		));
	}

	private static final String KEY_START = "start";
	private static final String KEY_END = "end";

	// Locations ---------------------------------------------------------------

	@Override
	public int getTokenOffset() {
		return ((Number) currentToken.get(Scanner.KEY_START)).intValue();
	}

	@Override
	public int getTokenLength() {
		return (((Number)currentToken.get(Scanner.KEY_END)).intValue()) - this.getTokenOffset();
	}

}

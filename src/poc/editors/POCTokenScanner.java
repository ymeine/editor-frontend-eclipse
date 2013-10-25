package poc.editors;

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

import poc.Backend;
import poc.document.POCDocument;

public class POCTokenScanner implements ITokenScanner {

	// Tokens ------------------------------------------------------------------

	private Iterator<Map<String, Object>> tokensIterator = null;
	private Map<String, Object> currentToken = null;

	// Styles ------------------------------------------------------------------

	private Map<String, Object> defaultStyle = null;
	private Map<String, Object> styles = null;



	public POCTokenScanner() {}



	/***************************************************************************
	 * Data update
	 **************************************************************************/

	private static final String ARGUMENT_OFFSET = "offset";

	private static final String METHOD_HIGHLIGHT = "highlight";
	private static final String KEY_TOKENS = "ranges";

	@Override
	@SuppressWarnings("unchecked")
	public void setRange(IDocument document, int offset, int length) {
		POCDocument doc = (POCDocument) document;

		try {

			// Styles ----------------------------------------------------------
			//this.getStylesheet(mode);
			Map<String, Object> stylesheet = (Map<String, Object>) Backend.get().service(doc, METHOD_STYLESHEET);
			this.defaultStyle = (Map<String, Object>) stylesheet.get(KEY_DEFAULT_STYLE);
			this.styles = (Map<String, Object>) stylesheet.get(KEY_STYLES);

			// Tokens ----------------------------------------------------------
			Map<String, Object> argument = new HashMap<String, Object>();
			argument.put("wholeSource", true);
			argument.put(ARGUMENT_OFFSET, offset);
			argument.put("end", offset + length);

			Map<String, Object> result = Backend.get().service(doc, METHOD_HIGHLIGHT, argument);
			
			List<Map<String, Object>> tokens = (List<Map<String, Object>>) result.get(KEY_TOKENS);

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

//	private static boolean safeCache = true;
//	private Map<String, Object> stylesheets = new HashMap<String, Object>();
//
//	// TODO Create or find a generic cache utility. Integrate this cache utility in the Backend class? It would make sense!! This is not optmize communication with the backend, which should implement a generic cache system
//	@SuppressWarnings("unchecked")
//	private void getStylesheet(POCDocument document) throws IOException {
//		Map<String, Object> stylesheet = null;
//
//		// Cache ---------------------------------------------------------------
//		if (stylesheets.containsKey(mode)) {
//			if (safeCache) {
//				Map<String, Object> options = new HashMap<String, Object>();
//				options.put("checkCache", true);
//				options.put("sendIfObsolete", true);
//
//				Map<String, Object> response;
//				try {
//					response = Backend.get().editor("mode", METHOD_STYLESHEET, options);
//					if ((Boolean)response.get("obsolete")) {
//						stylesheet = (Map<String, Object>) response.get(KEY_STYLESHEET);
//						stylesheets.put(mode, stylesheet);
//					} else {
//						stylesheet = (Map<String, Object>) stylesheets.get(mode);
//					}
//				} catch (JsonSyntaxException | ParseException
//						| BackendException e) {
//					e.printStackTrace();
//				}
//
//			} else {
//				stylesheet = (Map<String, Object>) stylesheets.get(mode);
//			}
//		} else {
//			try {
//				stylesheet = (Map<String, Object>) Backend.get().service(document, METHOD_STYLESHEET).get(KEY_STYLESHEET);
//				stylesheets.put(mode, stylesheet);
//			} catch (JsonSyntaxException | ParseException | BackendException e) {
//				e.printStackTrace();
//			}
//		}
//
//
//		// Stylesheet extractions ----------------------------------------------
//		defaultStyle = (Map<String, Object>) stylesheet.get(KEY_DEFAULT_STYLE);
//		styles = (Map<String, Object>) stylesheet.get(KEY_STYLES);
//	}



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
		String type = (String) this.currentToken.get(KEY_STYLE);

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

		Map<String, Object> rgb = (Map<String, Object>) style.get(KEY_COLOR);
		if (rgb == null) {
			rgb = (Map<String, Object>) defaultStyle.get(KEY_COLOR);
		}
		return new TextAttribute(new Color(
			Display.getCurrent(),
			((Number)rgb.get(KEY_RED)).intValue(),
			((Number)rgb.get(KEY_GREEN)).intValue(),
			((Number)rgb.get(KEY_BLUE)).intValue()
		));
	}

	private static final String KEY_START = "start";
	private static final String KEY_END = "end";

	// Locations ---------------------------------------------------------------

	@Override
	public int getTokenOffset() {
		return ((Number) currentToken.get(KEY_START)).intValue();
	}

	@Override
	public int getTokenLength() {
		return (((Number)currentToken.get(KEY_END)).intValue()) - this.getTokenOffset();
	}

}

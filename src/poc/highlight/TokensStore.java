package poc.highlight;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This class provides all different tokens. Colors used for syntax highlighting
 * are hard-coded as static members of the class
 *
 * @author flongo
 *
 */
public class TokensStore {

	private static TokensStore singleton = null;

	public static String DEFAULT = "default";
	public static int[] DEFAULT_COLOR = { 0, 0, 0 };

	public static String CONTAINER = "container";
	public static int[] CONTAINER_COLOR = null;

	public static String STATEMENT = "statement";
	public static int[] STATEMENT_COLOR = { 200, 20, 100 };

	public static String STATEMENT_ARGS = "statement_args";
	public static int[] STATEMENT_ARGS_COLOR = { 0, 0, 0 };

	public static String COMMENT = "comment";
	public static int[] COMMENT_COLOR = { 160, 160, 160 };

	public static String EXPRESSION = "expression";
	public static int[] EXPRESSION_COLOR = { 255, 0, 0 };

	public static String EXPRESSION_ARGS = "expression_args";
	public static int[] EXPRESSION_ARGS_COLOR = { 0, 0, 0 };

	public static String STRING = "string";
	public static int[] STRING_COLOR = { 93, 144, 205 };

	public static String TAG = "tag";
	public static int[] TAG_COLOR = { 136, 18, 128 };

	public static String TAG_ATTRIBUTE = "tag_attribute";
	public static int[] TAG_ATTRIBUTE_COLOR = { 153, 69, 0 };

	public static String TAG_ATTRIBUTE_EQUAL = "tag_attribute_equal";
	public static int[] TAG_ATTRIBUTE_EQUAL_COLOR = { 136, 18, 128 };

	public static String NUMBER = "number";
	public static int[] NUMBER_COLOR = { 0, 0, 255 };

	public static String BOOLEAN = "boolean";
	public static int[] BOOLEAN_COLOR = { 0, 0, 255 };

	public static String OPERATOR = "operator";
	public static int[] OPERATOR_COLOR = { 110, 110, 110 };

	public static String FUNCTION = "function";
	public static int[] FUNCTION_COLOR = { 20, 127, 10 };

	public static String KEY = "key";
	public static int[] KEY_COLOR = { 20, 127, 10 };

	public static String OBJECT = "object";
	public static int[] OBJECT_COLOR = { 0, 0, 0 };

	public static String ARRAY = "array";
	public static int[] ARRAY_COLOR = { 0, 0, 0 };

	private Map<String, int[]> colorMap = new HashMap<String, int[]>();

	public TokensStore() {
		this.colorMap.put(TokensStore.DEFAULT, TokensStore.DEFAULT_COLOR);
		this.colorMap.put(TokensStore.CONTAINER, TokensStore.CONTAINER_COLOR);
		this.colorMap.put(TokensStore.STATEMENT, TokensStore.STATEMENT_COLOR);
		this.colorMap.put(TokensStore.STATEMENT_ARGS, TokensStore.STATEMENT_ARGS_COLOR);
		this.colorMap.put(TokensStore.COMMENT, TokensStore.COMMENT_COLOR);
		this.colorMap.put(TokensStore.EXPRESSION, TokensStore.EXPRESSION_COLOR);
		this.colorMap.put(TokensStore.EXPRESSION_ARGS, TokensStore.EXPRESSION_ARGS_COLOR);
		this.colorMap.put(TokensStore.STRING, TokensStore.STRING_COLOR);
		this.colorMap.put(TokensStore.TAG, TokensStore.TAG_COLOR);
		this.colorMap.put(TokensStore.TAG_ATTRIBUTE, TokensStore.TAG_ATTRIBUTE_COLOR);
		this.colorMap.put(TokensStore.TAG_ATTRIBUTE_EQUAL, TokensStore.TAG_ATTRIBUTE_EQUAL_COLOR);
		this.colorMap.put(TokensStore.NUMBER, TokensStore.NUMBER_COLOR);
		this.colorMap.put(TokensStore.BOOLEAN, TokensStore.BOOLEAN_COLOR);
		this.colorMap.put(TokensStore.OPERATOR, TokensStore.OPERATOR_COLOR);
		this.colorMap.put(TokensStore.FUNCTION, TokensStore.FUNCTION_COLOR);
		this.colorMap.put(TokensStore.KEY, TokensStore.KEY_COLOR);
		this.colorMap.put(TokensStore.OBJECT, TokensStore.OBJECT_COLOR);
		this.colorMap.put(TokensStore.ARRAY, TokensStore.ARRAY_COLOR);
	}

	public static TokensStore get() {
		if (singleton == null) {
			TokensStore.singleton = new TokensStore();
		}
		return TokensStore.singleton;
	}

	public RichToken getToken(String type, int offset, int length) {
		RichToken returnToken = this.getToken(type);
		returnToken.setOffset(offset);
		returnToken.setLength(length);
		return returnToken;
	}

	public RichToken getToken(String type) {
		int[] rgb = this.colorMap.get(type);
		RichToken returnToken;
		if (rgb != null) {
			returnToken = new RichToken(new TextAttribute(new Color(Display.getCurrent(), rgb[0], rgb[1], rgb[2])));
		} else {
			returnToken = new RichToken();
		}
		returnToken.setType(type);
		return returnToken;
	}

}

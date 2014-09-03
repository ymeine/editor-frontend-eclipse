package com.ariatemplates.tools.ide.modes.athtml.highlighting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.PatternRule;

import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Array;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Attribute;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Expression;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Function;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Key;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Object;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Statement;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Tag;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Word;



/**
 * This class allows to retrieve rules of all the supported types
 *
 * @author flongo
 *
 */
public class RulesStore {

	public static int MULTILINE_COMMENT = 1;
	public static int SINGLELINE_COMMENT = 2;
	public static int STATEMENT = 3;
	public static int EXPRESSION = 4;
	public static int STRING_DOUBLE = 5;
	public static int STRING_SINGLE = 6;
	public static int STRING_COMPLEX = 7;
	public static int TAG = 8;
	public static int TAG_ATTRIBUTE = 9;
	public static int NUMBER = 10;
	public static int BOOLEAN = 11;
	public static int OPERATOR = 12;
	public static int FUNCTION = 13;
	public static int KEY = 14;
	public static int OBJECT = 15;
	public static int ARRAY = 16;

	private static RulesStore singleton = null;

	private TokensStore tokenStore = TokensStore.get();

	public static RulesStore get() {
		if (singleton == null) {
			RulesStore.singleton = new RulesStore();
		}
		return RulesStore.singleton;
	}

	/**
	 * @return the default rules
	 */
	public List<IRule> getRules() {
		int[] defaultRules = { RulesStore.MULTILINE_COMMENT, RulesStore.SINGLELINE_COMMENT, RulesStore.STATEMENT, RulesStore.EXPRESSION,
				RulesStore.STRING_COMPLEX, RulesStore.TAG };
		return this.getRules(defaultRules);
	}

	/**
	 *
	 * @param types
	 *            rules types from the static class properties
	 * @return the rules corresponding to the specified types
	 */
	public List<IRule> getRules(int[] types) {
		List<IRule> rules = new ArrayList<IRule>();
		for (int i = 0; i < types.length; i++) {
			rules.add(this.getRule(types[i]));
		}
		return rules;
	}

	/**
	 *
	 * @param type
	 *            rule type from the static class properties
	 * @return the rule corresponding to the specified type
	 */
	public IRule getRule(int type) {
		if (type == RulesStore.MULTILINE_COMMENT) {
			return new MultiLineRule("/*", "*/", this.tokenStore.getToken(TokensStore.COMMENT), (char) 0, false);
		}
		if (type == RulesStore.SINGLELINE_COMMENT) {
			return new EndOfLineRule("//", this.tokenStore.getToken(TokensStore.COMMENT));
		}
		if (type == RulesStore.STATEMENT) {
			return new Statement();
		}
		if (type == RulesStore.EXPRESSION) {
			return new Expression();
		}
		if (type == RulesStore.STRING_DOUBLE) {
			return new PatternRule("\"", "\"", this.tokenStore.getToken(TokensStore.STRING), '\\', false);
		}
		if (type == RulesStore.STRING_SINGLE) {
			return new PatternRule("'", "'", this.tokenStore.getToken(TokensStore.STRING), '\\', false);
		}
		if (type == RulesStore.STRING_COMPLEX) {
			return new com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.StringRule();
		}
		if (type == RulesStore.TAG) {
			return new Tag();
		}
		if (type == RulesStore.TAG_ATTRIBUTE) {
			return new Attribute();
		}
		if (type == RulesStore.NUMBER) {
			return new NumberRule(this.tokenStore.getToken(TokensStore.NUMBER));
		}
		if (type == RulesStore.BOOLEAN) {
			String[] words = { "true", "false" };
			return new Word(words, this.tokenStore.getToken(TokensStore.BOOLEAN));
		}
		if (type == RulesStore.OPERATOR) {
			String[] words = { "+", "-", "%", "*", "|", "&", "=", "<", ">", "!=" };
			return new Word(words, this.tokenStore.getToken(TokensStore.OPERATOR));
		}
		if (type == RulesStore.FUNCTION) {
			return new Function();
		}
		if (type == RulesStore.KEY) {
			return new Key();
		}
		if (type == RulesStore.OBJECT) {
			return new Object();
		}
		if (type == RulesStore.ARRAY) {
			return new Array();
		}
		return null;
	}

	/**
	 * @return the rules corresponding to the primitive types like strings,
	 *         arrays, objects, numbers and booleans
	 */
	public List<IRule> getPrimitiveRules() {
		int[] primitiveTypes = { RulesStore.MULTILINE_COMMENT, RulesStore.SINGLELINE_COMMENT, RulesStore.STRING_DOUBLE, RulesStore.STRING_SINGLE,
				RulesStore.NUMBER, RulesStore.BOOLEAN, RulesStore.OPERATOR, RulesStore.FUNCTION, RulesStore.OBJECT, RulesStore.ARRAY };
		return this.getRules(primitiveTypes);
	}

}

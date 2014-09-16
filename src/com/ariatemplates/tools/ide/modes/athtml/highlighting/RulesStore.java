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

	private static RulesStore singleton = null;
	public static RulesStore get() {
		if (singleton == null) {
			RulesStore.singleton = new RulesStore();
		}
		return RulesStore.singleton;
	}



	public static final int MULTILINE_COMMENT = 1;
	public static final int SINGLELINE_COMMENT = 2;
	public static final int STATEMENT = 3;
	public static final int EXPRESSION = 4;
	public static final int STRING_DOUBLE = 5;
	public static final int STRING_SINGLE = 6;
	public static final int STRING_COMPLEX = 7;
	public static final int TAG = 8;
	public static final int TAG_ATTRIBUTE = 9;
	public static final int NUMBER = 10;
	public static final int BOOLEAN = 11;
	public static final int OPERATOR = 12;
	public static final int FUNCTION = 13;
	public static final int KEY = 14;
	public static final int OBJECT = 15;
	public static final int ARRAY = 16;



	private TokensStore tokenStore = TokensStore.get();



	/**
	 * @return the default rules
	 */
	public List<IRule> getRules() {
		int[] defaultRules = {
			RulesStore.MULTILINE_COMMENT,
			RulesStore.SINGLELINE_COMMENT,
			RulesStore.STATEMENT,
			RulesStore.EXPRESSION,
			RulesStore.STRING_COMPLEX,
			RulesStore.TAG
		};

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
		for (int type: types) {
			rules.add(this.getRule(type));
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
		switch (type) {
			case RulesStore.MULTILINE_COMMENT:
				return new MultiLineRule("/*", "*/", this.tokenStore.getToken(TokensStore.COMMENT), (char) 0, false);

			case RulesStore.SINGLELINE_COMMENT:
				return new EndOfLineRule("//", this.tokenStore.getToken(TokensStore.COMMENT));

			case RulesStore.STATEMENT:
				return new Statement();

			case RulesStore.EXPRESSION:
				return new Expression();

			case RulesStore.STRING_DOUBLE:
				return new PatternRule("\"", "\"", this.tokenStore.getToken(TokensStore.STRING), '\\', false);

			case RulesStore.STRING_SINGLE:
				return new PatternRule("'", "'", this.tokenStore.getToken(TokensStore.STRING), '\\', false);

			case RulesStore.STRING_COMPLEX:
				return new com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.StringRule();

			case RulesStore.TAG:
				return new Tag();

			case RulesStore.TAG_ATTRIBUTE:
				return new Attribute();

			case RulesStore.NUMBER:
				return new NumberRule(this.tokenStore.getToken(TokensStore.NUMBER));

			case RulesStore.BOOLEAN:
				String[] keywords = { "true", "false" };
				return new Word(keywords, this.tokenStore.getToken(TokensStore.BOOLEAN));

			case RulesStore.OPERATOR:
				String[] operators = { "+", "-", "%", "*", "|", "&", "=", "<", ">", "!=" };
				return new Word(operators, this.tokenStore.getToken(TokensStore.OPERATOR));

			case RulesStore.FUNCTION:
				return new Function();

			case RulesStore.KEY:
				return new Key();

			case RulesStore.OBJECT:
				return new Object();

			case RulesStore.ARRAY:
				return new Array();

			default:
				return null;
		}
	}

	/**
	 * @return the rules corresponding to the primitive types like strings,
	 *         arrays, objects, numbers and booleans
	 */
	public List<IRule> getPrimitiveRules() {
		int[] primitiveTypes = {
			RulesStore.MULTILINE_COMMENT,
			RulesStore.SINGLELINE_COMMENT,
			RulesStore.STRING_DOUBLE,
			RulesStore.STRING_SINGLE,
			RulesStore.NUMBER,
			RulesStore.BOOLEAN,
			RulesStore.OPERATOR,
			RulesStore.FUNCTION,
			RulesStore.OBJECT,
			RulesStore.ARRAY
		};

		return this.getRules(primitiveTypes);
	}

}

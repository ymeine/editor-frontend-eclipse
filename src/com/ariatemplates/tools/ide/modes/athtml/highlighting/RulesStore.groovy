package com.ariatemplates.tools.ide.modes.athtml.highlighting



import org.eclipse.jface.text.rules.EndOfLineRule
import org.eclipse.jface.text.rules.IRule
import org.eclipse.jface.text.rules.MultiLineRule
import org.eclipse.jface.text.rules.NumberRule
import org.eclipse.jface.text.rules.PatternRule

import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Array
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Attribute
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Expression
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Function
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Key
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Object
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Statement
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Tag
import com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.Word



/**
 * This class allows to retrieve rules of all the supported types
 *
 * @author flongo
 *
 */
class RulesStore {

	private static singleton
	static get() {
		this.singleton = this.singleton ?: new RulesStore()
	}

	static final MULTILINE_COMMENT = 1
	static final SINGLELINE_COMMENT = 2
	static final STATEMENT = 3
	static final EXPRESSION = 4
	static final STRING_DOUBLE = 5
	static final STRING_SINGLE = 6
	static final STRING_COMPLEX = 7
	static final TAG = 8
	static final TAG_ATTRIBUTE = 9
	static final NUMBER = 10
	static final BOOLEAN = 11
	static final OPERATOR = 12
	static final FUNCTION = 13
	static final KEY = 14
	static final OBJECT = 15
	static final ARRAY = 16

	private tokenStore = TokensStore.get()



	def RulesStore() {
		super()
		def cl = this.class

		this.builders = [
			(cl.MULTILINE_COMMENT): {
				new MultiLineRule("/*", "*/", this.tokenStore.getToken(TokensStore.COMMENT), (0 as char), false)
			},
			(cl.SINGLELINE_COMMENT): {
				new EndOfLineRule("//", this.tokenStore.getToken(TokensStore.COMMENT))
			},

			(cl.STATEMENT): {new Statement()},
			(cl.EXPRESSION): {new Expression()},

			(cl.STRING_DOUBLE): {
				new PatternRule("\"", "\"", this.tokenStore.getToken(TokensStore.STRING), '\\', false)
			},
			(cl.STRING_SINGLE): {
				new PatternRule("'", "'", this.tokenStore.getToken(TokensStore.STRING), '\\', false)
			},
			(cl.STRING_COMPLEX): {
				new com.ariatemplates.tools.ide.modes.athtml.highlighting.rules.StringRule()
			},

			(cl.TAG): {new Tag()},
			(cl.TAG_ATTRIBUTE): {new Attribute()},

			(cl.NUMBER): {
				new NumberRule(this.tokenStore.getToken(TokensStore.NUMBER))
			},
			(cl.BOOLEAN): {
				def words = ["true", "false"]
				new Word(words, this.tokenStore.getToken(TokensStore.BOOLEAN))
			},
			(cl.OPERATOR): {
				def words = ["+", "-", "%", "*", "|", "&", "=", "<", ">", "!="]
				new Word(words, this.tokenStore.getToken(TokensStore.OPERATOR))
			},

			(cl.FUNCTION): {new Function()},
			(cl.KEY): {new Key()},
			(cl.OBJECT): {new Object()},
			(cl.ARRAY): {new Array()}
		]
	}

	/**
	 * @return the default rules
	 */
	def getRules() {
		def cl = this.class

		def defaultRules = [
			cl.MULTILINE_COMMENT,
			cl.SINGLELINE_COMMENT,
			cl.STATEMENT,
			cl.EXPRESSION,
			cl.STRING_COMPLEX,
			cl.TAG
		]

		this.getRules defaultRules
	}

	/**
	 *
	 * @param types
	 *            rules types from the static class properties
	 * @return the rules corresponding to the specified types
	 */
	def getRules(types) {
		types.collect { type -> this.getRule type }
	}

	/**
	 *
	 * @param type
	 *            rule type from the static class properties
	 * @return the rule corresponding to the specified type
	 */
	def getRule(type) {(this.builders[type] ?: {null})()}

	/**
	 * @return the rules corresponding to the primitive types like strings,
	 *         arrays, objects, numbers and booleans
	 */
	public List<IRule> getPrimitiveRules() {
		def cl = this.class

		def primitiveTypes = [
			cl.MULTILINE_COMMENT,
			cl.SINGLELINE_COMMENT,
			cl.STRING_DOUBLE,
			cl.STRING_SINGLE,
			cl.NUMBER,
			cl.BOOLEAN,
			cl.OPERATOR,
			cl.FUNCTION,
			cl.OBJECT,
			cl.ARRAY
		]

		this.getRules primitiveTypes
	}
}

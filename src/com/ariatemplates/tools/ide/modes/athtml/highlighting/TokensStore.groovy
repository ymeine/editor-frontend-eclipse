package com.ariatemplates.tools.ide.modes.athtml.highlighting



import org.eclipse.jface.text.TextAttribute
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.widgets.Display

import com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.Rich



/**
 * This class provides all different tokens. Colors used for syntax highlighting
 * are hard-coded as static members of the class
 *
 * @author flongo
 *
 */
class TokensStore {
	private static singleton

	static get() {
		this.singleton = this.singleton ?: new TokensStore()
	}



	static DEFAULT = "default"; static DEFAULT_COLOR = [0, 0, 0]

	static CONTAINER = "container"; static CONTAINER_COLOR = null
	static STATEMENT = "statement"; static STATEMENT_COLOR = [200, 20, 100]
	static STATEMENT_ARGS = "statement_args"; static STATEMENT_ARGS_COLOR = [0, 0, 0]
	static COMMENT = "comment"; static COMMENT_COLOR = [160, 160, 160]
	static EXPRESSION = "expression"; static EXPRESSION_COLOR = [255, 0, 0]
	static EXPRESSION_ARGS = "expression_args"; static EXPRESSION_ARGS_COLOR = [0, 0, 0]
	static STRING = "string"; static STRING_COLOR = [93, 144, 205]
	static TAG = "tag"; static TAG_COLOR = [136, 18, 128]
	static TAG_ATTRIBUTE = "tag_attribute"; static TAG_ATTRIBUTE_COLOR = [153, 69, 0]
	static TAG_ATTRIBUTE_EQUAL = "tag_attribute_equal"; static TAG_ATTRIBUTE_EQUAL_COLOR = [136, 18, 128]
	static NUMBER = "number"; static NUMBER_COLOR = [0, 0, 255]
	static BOOLEAN = "boolean"; static BOOLEAN_COLOR = [0, 0, 255]
	static OPERATOR = "operator"; static OPERATOR_COLOR = [110, 110, 110]
	static FUNCTION = "function"; static FUNCTION_COLOR = [20, 127, 10]
	static KEY = "key"; static KEY_COLOR = [20, 127, 10]
	static OBJECT = "object"; static OBJECT_COLOR = [0, 0, 0]
	static ARRAY = "array"; static ARRAY_COLOR = [0, 0, 0]

	private colorMap

	TokensStore() {
		def cl = this.class
		this.colorMap = [
			(cl.DEFAULT): cl.DEFAULT_COLOR,
			(cl.CONTAINER): cl.CONTAINER_COLOR,
			(cl.STATEMENT): cl.STATEMENT_COLOR,
			(cl.STATEMENT_ARGS): cl.STATEMENT_ARGS_COLOR,
			(cl.COMMENT): cl.COMMENT_COLOR,
			(cl.EXPRESSION): cl.EXPRESSION_COLOR,
			(cl.EXPRESSION_ARGS): cl.EXPRESSION_ARGS_COLOR,
			(cl.STRING): cl.STRING_COLOR,
			(cl.TAG): cl.TAG_COLOR,
			(cl.TAG_ATTRIBUTE): cl.TAG_ATTRIBUTE_COLOR,
			(cl.TAG_ATTRIBUTE_EQUAL): cl.TAG_ATTRIBUTE_EQUAL_COLOR,
			(cl.NUMBER): cl.NUMBER_COLOR,
			(cl.BOOLEAN): cl.BOOLEAN_COLOR,
			(cl.OPERATOR): cl.OPERATOR_COLOR,
			(cl.FUNCTION): cl.FUNCTION_COLOR,
			(cl.KEY): cl.KEY_COLOR,
			(cl.OBJECT): cl.OBJECT_COLOR,
			(cl.ARRAY): cl.ARRAY_COLOR
		]
	}

	def getToken(type, offset=null, length=null) {
		def rgb = this.colorMap[type]

		def token
		if (rgb != null) {
			token = new Rich(new TextAttribute(new Color(Display.current, *rgb)))
		} else {
			token = new Rich()
		}

		token.type = type

		if (offset != null) token.offset = offset
		if (length != null) token.length = length

		token
	}
}

package com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens



import org.eclipse.jface.text.rules.IToken
import org.eclipse.jface.text.rules.Token



/**
 * This class adds extra information to the Token class in order to interact
 * with the rule-based parser that is used as tokenizer. in order to allow a
 * recursive structure, each token is decorated with a "children " property that
 * contains a list of tokens. This structure, once again, is compatible with the
 * parser
 *
 * @author flongo
 *
 */
class Rich extends Token {

	static UNDEFINED_INT = -1
	static UNDEFINED = new Rich(false)

	def offset = -1
	def children = []
	def length = -1
	def type

	private defined = true



	Rich(data, offset, length) {
		super(data)
		this.offset = offset
		this.length = length
	}

	Rich(data=null) {
		super(data)
	}
	
	Rich(Boolean defined) {
		super(null)
		this.defined = defined
	}



	boolean isUndefined() {!this.defined}
	def addChild(child) {this.children.add child}
	def hasChildren() {!this.children.isEmpty()}

	def clone() {
		def token = new Rich()

		token.data = this.data
		token.length = this.length
		token.offset = this.offset
		token.defined = this.defined
		token.children = this.children
		token.type = this.type

		token
	}

	def popChild() {
		this.children.remove this.children.size() - 1
	}
}

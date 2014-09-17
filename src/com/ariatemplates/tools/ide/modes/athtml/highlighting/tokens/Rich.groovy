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



	public Rich(Object data, offset, length) {
		super(data)
		this.offset = offset
		this.length = length
	}
	
	public Rich() {
		super(null)
	}
	
	public Rich(Boolean defined) {
		super(null)
		this.defined = defined
	}
	
	public Rich(Object data) {
		super(data)
	}

	
	
	boolean isUndefined() {!this.defined}
	def addChild(child) {this.children += child}
	def hasChildren() {!this.children.isEmpty()}

	def clone() {
		def newToken = new Rich()

		newToken.setData this.getData()

		newToken.length = this.length
		newToken.offset = this.offset
		newToken.defined = this.defined
		newToken.children = this.children
		newToken.type = this.type

		newToken
	}

	def popChild() {
		this.children.remove this.children.size() - 1
	}
}

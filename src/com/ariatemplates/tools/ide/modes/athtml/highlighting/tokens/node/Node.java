package com.ariatemplates.tools.ide.modes.athtml.highlighting.tokens.node;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;



public class Node extends Token {

	public static int UNDEFINED_INT = -1;
	public static Node UNDEFINED = new Node(false);

	private List<IToken> children = new ArrayList<IToken>();
	private int offset = -1;
	private int length = -1;

	private boolean defined = true;
	private String type;



	public Node(Object data, int offset, int length) {
		super(data);
		this.offset = offset;
		this.length = length;
	}

	public Node() {
		super(null);
	}

	public Node(Boolean defined) {
		super(null);
		this.setDefined(defined);
	}

	public Node(Object data) {
		super(data);
	}



	private void setDefined(boolean defined) {
		this.defined = defined;
	}

	@Override
	public boolean isUndefined() {
		return !this.defined;
	}



	public void addChild(IToken child) {
		this.children.add(child);
	}

	public Boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public List<IToken> getChildren() {
		return this.children;
	}

	public void setChildren(List<IToken> children) {
		this.children = children;
	}

	public void popChild() {
		this.children.remove(this.children.size() - 1);
	}



	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}


	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}



	public Node clone() {
		Node newToken = new Node();

		newToken.setData(this.getData());

		newToken.setLength(this.length);
		newToken.setOffset(this.offset);
		newToken.setDefined(this.defined);
		newToken.setChildren(this.children);
		newToken.setType(this.type);

		return newToken;
	}
}

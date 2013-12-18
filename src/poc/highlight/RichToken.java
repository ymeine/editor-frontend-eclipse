package poc.highlight;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

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
public class RichToken extends Token {

	private List<IToken> children = new ArrayList<IToken>();
	private int offset = -1;
	private int length = -1;

	public static int UNDEFINED_INT = -1;

	public static RichToken UNDEFINED = new RichToken(false);

	private boolean defined = true;
	private String type;

	public RichToken(Object data, int offset, int length) {
		super(data);
		this.offset = offset;
		this.length = length;
	}

	public RichToken() {
		super(null);
	}

	public RichToken(Boolean defined) {
		super(null);
		this.setDefined(defined);
	}

	private void setDefined(boolean defined) {
		this.defined = defined;
	}

	@Override
	public boolean isUndefined() {
		return !this.defined;
	}

	public RichToken(Object data) {
		super(data);
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

	public RichToken clone() {
		RichToken newToken = new RichToken();
		newToken.setData(this.getData());
		newToken.setLength(this.length);
		newToken.setOffset(this.offset);
		newToken.setDefined(this.defined);
		newToken.setChildren(this.children);
		newToken.setType(this.type);
		return newToken;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void popChild() {
		this.children.remove(this.children.size() - 1);
	}
}

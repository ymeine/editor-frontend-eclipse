package com.ariatemplates.tools.ide.modes.athtml.highlighting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Buffer {
	public List<Integer> content;
	
	public Buffer() {
		this.content = new ArrayList<Integer>();
	}

	
	
	public int add(int character) {
		this.content.add(character);
		return character;
	}
	
	public int pop() {
		int lastIndex = this.lastIndex();
		int removed = 	this.content.get(lastIndex);
		this.content.remove(lastIndex);
		return removed;
	}
	
	public Buffer clear() {
		this.content.clear();
		return this;
	}
	
	public int last() {
		return this.content.get(this.lastIndex());
	}

	public int lastIndex() {
		return this.content.size() - 1;
	}
	
	
	public String toString() {
		return Arrays.toString(this.content.toArray(new Integer[this.content.size()]));
	}
}

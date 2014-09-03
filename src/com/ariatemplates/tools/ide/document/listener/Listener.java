package com.ariatemplates.tools.ide.document.listener;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import com.ariatemplates.tools.ide.document.document.Document;
import com.ariatemplates.tools.ide.document.listener.Listener;


public class Listener implements IDocumentListener {

	private Document document = null;

	public Listener(Document document) {
		this.document = document;
	}

	private final static String KEY_START = "start";
	private final static String KEY_END = "end";
	private final static String KEY_SOURCE = "source";

	@Override
	public void documentChanged(DocumentEvent event) {
		Map<String, Object> argument = new HashMap<String, Object>();
		argument.put(Listener.KEY_START, event.getOffset());
		argument.put(Listener.KEY_END, event.getOffset() + event.getLength());
		argument.put(Listener.KEY_SOURCE, event.getText());

		document.addSourceChange(argument);

		document.clearMarkerAnnotations();
	}

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	@Override public void documentAboutToBeChanged(DocumentEvent event) {}
}

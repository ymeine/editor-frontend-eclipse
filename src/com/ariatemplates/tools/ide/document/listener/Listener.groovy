package com.ariatemplates.tools.ide.document.listener



import org.eclipse.jface.text.DocumentEvent
import org.eclipse.jface.text.IDocumentListener

import com.ariatemplates.tools.ide.document.document.Document



class Listener implements IDocumentListener {
	private Document document

	def Listener(Document document) {
		this.document = document
	}

	void documentChanged(DocumentEvent event) {
		document.addSourceChange [
			"start": event.getOffset(),
			"end": event.getOffset() + event.getLength(),
			"source": event.getText()
		]

		document.clearMarkerAnnotations()
	}

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	void documentAboutToBeChanged(DocumentEvent event) {}
}

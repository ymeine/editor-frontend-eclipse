package com.ariatemplates.tools.ide.document.listener



import org.eclipse.jface.text.DocumentEvent
import org.eclipse.jface.text.IDocumentListener



class Listener implements IDocumentListener {
	private document

	Listener(document) {
		this.document = document
	}

	void documentChanged(DocumentEvent event) {
		def offset = event.offset

		document.addSourceChange([
			"start": offset,
			"end": offset + event.length,
			"source": event.text
		])

		document.clearMarkerAnnotations()
	}

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	void documentAboutToBeChanged(DocumentEvent event) {}
}

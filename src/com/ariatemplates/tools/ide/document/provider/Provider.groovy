package com.ariatemplates.tools.ide.document.provider



import org.eclipse.core.runtime.CoreException
import org.eclipse.jface.text.IDocument
import org.eclipse.ui.editors.text.FileDocumentProvider
import org.eclipse.ui.part.FileEditorInput

import com.ariatemplates.tools.ide.backend.backend.Backend
import com.ariatemplates.tools.ide.backend.exception.BackendException
import com.ariatemplates.tools.ide.document.document.Document
import com.ariatemplates.tools.ide.document.listener.Listener
import com.ariatemplates.tools.ide.document.partitioner.Partitioner



class Provider extends FileDocumentProvider {

	// FIXME Should be inferred from extension or something like that
	private static final mode = "athtml"



	protected IDocument createDocument(Object element) {
		// Document creation (client side) -------------------------------------

		// In practice `element` is a FileEditorInput
		Document document = super.createDocument element

		if (document == null) {
			return null
		}

		document.setFile (element as FileEditorInput).getFile()

		// Document registration (backend side) --------------------------------

		try {
			def result = Backend.get().editor "init", [
				"mode": mode,
				"source": document.get(),
				"extension": document.getFile().getFileExtension()
			]

			document.guid = result
		} catch (BackendException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Document configuration ----------------------------------------------

		document.setDocumentPartitioner new Partitioner(document)
		document.addDocumentListener new Listener(document)

		// Return --------------------------------------------------------------

		document
	}

	protected IDocument createEmptyDocument() {
		new Document()
	}
}

package com.ariatemplates.tools.ide.document.provider;



import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;

import com.ariatemplates.tools.ide.backend.backend.Backend;
import com.ariatemplates.tools.ide.backend.exception.BackendException;
import com.ariatemplates.tools.ide.document.document.Document;
import com.ariatemplates.tools.ide.document.listener.Listener;
import com.ariatemplates.tools.ide.document.partitioner.Partitioner;
import com.ariatemplates.tools.ide.document.provider.Provider;



public class Provider extends FileDocumentProvider {

	// FIXME Should be inferred from extension or something like that
	private static final String mode = "athtml";

	private static final String METHOD_INIT = "init";
	private static final String ARGUMENT_MODE = "mode";
	private static final String ARGUMENT_SOURCE = "source";
	private static final String ARGUMENT_EXTENSION = "extension";


	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		// Document creation (client side) -------------------------------------

		// In practice `element` is a FileEditorInput
		Document document = (Document) super.createDocument(element);

		if (document == null) {
			return null;
		}

		document.setFile(((FileEditorInput) element).getFile());

		// Document registration (backend side) --------------------------------

		try {
			Map<String, Object> argument = new HashMap<String, Object>();
			argument.put(Provider.ARGUMENT_MODE, Provider.mode);
			argument.put(Provider.ARGUMENT_SOURCE, document.get());
			argument.put(Provider.ARGUMENT_EXTENSION, document.getFile().getFileExtension());

			Map<String, Object> result = Backend.get().editor(Provider.METHOD_INIT, argument);

			document.setGUID(result);

		} catch (BackendException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Document configuration ----------------------------------------------

		document.setDocumentPartitioner(new Partitioner(document));
		document.addDocumentListener(new Listener(document));

		// Return --------------------------------------------------------------

		return document;
	}

	@Override
	protected IDocument createEmptyDocument() {
		return new Document();
	}
}

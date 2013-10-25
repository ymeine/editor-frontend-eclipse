package poc.document;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import poc.Backend;
import poc.BackendException;

public class POCDocumentProvider extends FileDocumentProvider {

	// FIXME Should be inferred from extension or something like that
	private static final String mode = "html";

	private static final String METHOD_INIT = "init";
	private static final String ARGUMENT_MODE = "mode";
	private static final String ARGUMENT_SOURCE = "source";

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		// Document creation (client side) -------------------------------------

		// In practice `element` is a FileDocumentInput
		POCDocument document = (POCDocument) super.createDocument(element);

		if (document == null) {
			return null;
		}

		// Document registration (backend side) --------------------------------

		try {
			Map<String, Object> argument = new HashMap<String, Object>();
			argument.put(POCDocumentProvider.ARGUMENT_MODE, mode);
			argument.put(POCDocumentProvider.ARGUMENT_SOURCE, document.get());

			Map<String, Object> result = Backend.get().editor(POCDocumentProvider.METHOD_INIT, argument);

			document.setGUID(result);

		} catch (BackendException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Document configuration ----------------------------------------------

		document.setDocumentPartitioner(new POCDocumentPartitioner());
		document.addDocumentListener(new POCDocumentListener(document));

		// Return --------------------------------------------------------------

		return document;
	}

	@Override
	protected IDocument createEmptyDocument() {
		return new POCDocument();
	}
}

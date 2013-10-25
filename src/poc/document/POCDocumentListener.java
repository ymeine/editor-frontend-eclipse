package poc.document;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import poc.Backend;
import poc.BackendException;

public class POCDocumentListener implements IDocumentListener {

	private POCDocument document = null;

	public POCDocumentListener(POCDocument document) {
		this.document = document;
	}

	private final static String METHOD_UPDATE = "update";
	private final static String KEY_START = "start";
	private final static String KEY_END = "end";
	private final static String KEY_SOURCE = "source";

	@Override
	public void documentChanged(DocumentEvent event) {
		try {
			Map<String, Object> argument = new HashMap<String, Object>();
			argument.put(POCDocumentListener.KEY_START, event.getOffset());
			argument.put(POCDocumentListener.KEY_END, event.getOffset() + event.getLength());
			argument.put(POCDocumentListener.KEY_SOURCE, event.getText());

			Backend.get().service(document, POCDocumentListener.METHOD_UPDATE, argument);
		} catch (BackendException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * Unused
	 **************************************************************************/
	
	@Override public void documentAboutToBeChanged(DocumentEvent event) {}
}

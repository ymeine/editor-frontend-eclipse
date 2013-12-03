package poc.document;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;


public class POCDocumentListener implements IDocumentListener {

	private POCDocument document = null;

	public POCDocumentListener(POCDocument document) {
		this.document = document;
	}

	private final static String KEY_START = "start";
	private final static String KEY_END = "end";
	private final static String KEY_SOURCE = "source";

	@Override
	public void documentChanged(DocumentEvent event) {
		Map<String, Object> argument = new HashMap<String, Object>();
		argument.put(POCDocumentListener.KEY_START, event.getOffset());
		argument.put(POCDocumentListener.KEY_END, event.getOffset() + event.getLength());
		argument.put(POCDocumentListener.KEY_SOURCE, event.getText());

		document.addSourceChange(argument);

		document.clearMarkerAnnotations();
	}

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	@Override public void documentAboutToBeChanged(DocumentEvent event) {}
}

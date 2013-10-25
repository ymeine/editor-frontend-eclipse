package poc.document;

import java.util.Map;

import org.eclipse.jface.text.Document;

public class POCDocument extends Document {

	private Map<String, Object> GUID = null;

	public Map<String, Object> getGUID() {
		return this.GUID;
	}

	public void setGUID(Map<String, Object> GUID) {
		this.GUID = GUID;
	}
}

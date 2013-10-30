package poc;

import java.util.Map;

public class BackendException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final static String KEY_MESSAGE = "msg";
	
	public static String buildMessage(Map<String, Object> content) {
		String msg = (String) content.get(BackendException.KEY_MESSAGE);
		content.remove(BackendException.KEY_MESSAGE);
		return msg + ": " + content;
	}
	
	public BackendException(Map<String, Object> content) {
		super(BackendException.buildMessage(content));
	}
}

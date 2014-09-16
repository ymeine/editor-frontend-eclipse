package com.ariatemplates.tools.ide.document.document;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;

import com.ariatemplates.tools.ide.backend.backend.Backend;
import com.ariatemplates.tools.ide.document.document.Document;



public class Document extends org.eclipse.jface.text.Document {

	private Map<String, Object> GUID = null;
	private IFile file = null;
	private final static String METHOD_UPDATE = "update";
	private List<Map<String, Object>> sourceChanges = new ArrayList<Map<String,Object>>();



	public Map<String, Object> getGUID() {
		return this.GUID;
	}

	public void setGUID(Map<String, Object> GUID) {
		this.GUID = GUID;
	}

	public IFile getFile() {
		return file;
	}

	public void setFile(IFile file) {
		this.file = file;
	}



	public void addSourceChange(Map<String, Object> entry) {
		this.sourceChanges.add(entry);
	}

	public void resetSourceChanges() {
		this.sourceChanges.clear();
	}

	public void updateSource() {
		try {
			Backend.get().service(this, Document.METHOD_UPDATE, this.sourceChanges);
			this.resetSourceChanges();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings("unchecked")
	public void addMarkerAnnotation(Map<String, ?> config, int severity) {
		try {
			IMarker marker = file.createMarker("com.ariatemplates.tools.ide.error");
			marker.setAttribute(IMarker.SEVERITY, severity);

			Map<String, ?> location = ((Map<String, ?>) config.get("location"));
			int charStart = ((Double) ((Map<String, ?>) location.get("start")).get("index")).intValue();
			int charEnd = ((Double) ((Map<String, ?>) location.get("end")).get("index")).intValue();
			marker.setAttribute(IMarker.CHAR_START, charStart);
			marker.setAttribute(IMarker.CHAR_END, charEnd);
			marker.setAttribute(IMarker.LINE_NUMBER, this.getLineOfOffset(charStart));
			String message = this.formatMessages((ArrayList<String>) config.get("messages"));
			marker.setAttribute(IMarker.MESSAGE, message);

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void clearMarkerAnnotations() {
		try {
			file.deleteMarkers("com.ariatemplates.tools.ide.error", true, 1);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public String formatMessages(ArrayList<String> messages) {
		Object[] messagesArray = (Object[]) messages.toArray();
		StringBuilder out = new StringBuilder();

		for (int index = 0; index < messagesArray.length; index++) {
			if (index > 0) {
				out.append("\n");
			}
			out.append("- ").append(messagesArray[index]);
		}
		return out.toString();
	}

	@SuppressWarnings("unchecked")
	public void addAllMarkerAnnotations(Map<String, Object> messages) {
		List<Map<String, ?>> errors = (List<Map<String, ?>>) messages.get("errors");
		for (Map<String, ?> err : errors) {
			this.addMarkerAnnotation(err, IMarker.SEVERITY_ERROR);
		}
		List<Map<String, ?>> warnings = (List<Map<String, ?>>) messages.get("warnings");
		for (Map<String, ?> warns : warnings) {
			this.addMarkerAnnotation(warns, IMarker.SEVERITY_WARNING);
		}
	}



}

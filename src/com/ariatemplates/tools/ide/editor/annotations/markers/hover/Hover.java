package com.ariatemplates.tools.ide.editor.annotations.markers.hover;



import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;



public class Hover implements IAnnotationHover {

	@Override
	public String getHoverInfo(ISourceViewer sourceViewer, int line) {
		StringBuilder hoverInfo = new StringBuilder();

		Iterator<?> iterator = sourceViewer.getAnnotationModel().getAnnotationIterator();

		while (iterator.hasNext()) {
			Object annotation = iterator.next();
			if (annotation instanceof MarkerAnnotation) {
				MarkerAnnotation marker = (MarkerAnnotation) annotation;
				try {
					int markerLine = (Integer) marker.getMarker().getAttribute(IMarker.LINE_NUMBER);
					if (markerLine == line) {
						String markerMessage = (String) marker.getMarker().getAttribute(IMarker.MESSAGE);
						if (hoverInfo.length() != 0) {
							hoverInfo.append("\n");
						}
						hoverInfo.append(markerMessage);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return hoverInfo.toString();
	}

}

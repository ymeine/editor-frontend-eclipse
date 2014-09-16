package com.ariatemplates.tools.ide.editor.annotations.markers.hover



import java.util.Iterator

import org.eclipse.core.resources.IMarker
import org.eclipse.core.runtime.CoreException
import org.eclipse.jface.text.source.IAnnotationHover
import org.eclipse.jface.text.source.ISourceViewer
import org.eclipse.ui.texteditor.MarkerAnnotation



class Hover implements IAnnotationHover {

	String getHoverInfo(ISourceViewer sourceViewer, int line) {
		def hoverInfo = new StringBuilder()

		def iterator = sourceViewer.getAnnotationModel().getAnnotationIterator()

		while (iterator.hasNext()) {
			def annotation = iterator.next()
			if (annotation instanceof MarkerAnnotation) {
				def marker = annotation as MarkerAnnotation;
				try {
					int markerLine = marker.getMarker().getAttribute IMarker.LINE_NUMBER

					if (markerLine == line) {
						def markerMessage = marker.getMarker().getAttribute IMarker.MESSAGE
						if (hoverInfo.length() != 0) {
							hoverInfo.append "\n"
						}
						hoverInfo.append markerMessage
					}
				} catch (CoreException e) {
					e.printStackTrace()
				}
			}
		}

		"$hoverInfo"
	}

}

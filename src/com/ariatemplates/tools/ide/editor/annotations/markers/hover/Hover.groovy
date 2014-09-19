package com.ariatemplates.tools.ide.editor.annotations.markers.hover



import org.eclipse.core.resources.IMarker
import org.eclipse.core.runtime.CoreException
import org.eclipse.jface.text.source.IAnnotationHover
import org.eclipse.jface.text.source.ISourceViewer
import org.eclipse.ui.texteditor.MarkerAnnotation



class Hover implements IAnnotationHover {

	String getHoverInfo(ISourceViewer sourceViewer, int line) {
		def hoverInfo = []

		def iterator = sourceViewer.annotationModel.annotationIterator

		while (iterator.hasNext()) {
			def annotation = iterator.next()
			if (annotation instanceof MarkerAnnotation) {
				def marker = annotation
				try {
					def markerLine = marker.marker.getAttribute IMarker.LINE_NUMBER

					if (markerLine == line) {
						def markerMessage = marker.marker.getAttribute IMarker.MESSAGE
						hoverInfo.add markerMessage
					}
				} catch (CoreException e) {
					e.printStackTrace()
				}
			}
		}

		hoverInfo.join("\n")
	}

}

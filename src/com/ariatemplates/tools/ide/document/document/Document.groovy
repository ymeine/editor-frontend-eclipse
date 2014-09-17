package com.ariatemplates.tools.ide.document.document



import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IMarker
import org.eclipse.core.runtime.CoreException
import org.eclipse.jface.text.BadLocationException

import com.ariatemplates.tools.ide.backend.backend.Backend



class Document extends org.eclipse.jface.text.Document {
	def guid
	IFile file

	private sourceChanges = []

	def addSourceChange(entry) {
		this.sourceChanges += entry
	}

	def resetSourceChanges() {
		this.sourceChanges.clear()
	}

	def updateSource() {
		try {
			Backend.get().service this, "update", this.sourceChanges
			this.resetSourceChanges()
		} catch (e) {
			e.printStackTrace()
		}
	}

	def addMarkerAnnotation(config, severity) {
		try {
			def marker = this.file.createMarker "com.ariatemplates.tools.ide.error"
			marker.setAttribute IMarker.SEVERITY, severity

			def location = config["location"]

			int charStart = location["start"]["index"]
			int charEnd = location["end"]["index"]
			marker.setAttribute IMarker.CHAR_START, charStart
			marker.setAttribute IMarker.CHAR_END, charEnd
			marker.setAttribute IMarker.LINE_NUMBER, this.getLineOfOffset(charStart)

			String message = this.formatMessages config["messages"]
			marker.setAttribute IMarker.MESSAGE, message
		} catch (CoreException e) {
			e.printStackTrace()
		} catch (BadLocationException e) {
			e.printStackTrace()
		}
	}

	def clearMarkerAnnotations() {
		try {
			this.file.deleteMarkers "com.ariatemplates.tools.ide.error", true, 1
		} catch (CoreException e) {
			e.printStackTrace()
		}
	}

	String formatMessages(messages) {
		def out = new StringBuilder()

		messages.eachWithIndex { message, index ->
			if (index > 0) {
				out.append "\n"
			}
			out.append("- ").append(message)
		}

		"$out"
	}

	def addAllMarkerAnnotations(messages) {
		def sets = [
			[
				"key": "errors",
				"severity": IMarker.SEVERITY_ERROR
			],
			[
				"key": "warnings",
				"severity": IMarker.SEVERITY_WARNING
			]
		]

		sets.each { config ->
			def severity = config["severity"]
			messages[config["key"]].each {message -> this.addMarkerAnnotation message, severity}
		}
	}
}

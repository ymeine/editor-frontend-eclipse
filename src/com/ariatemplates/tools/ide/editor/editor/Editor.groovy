package com.ariatemplates.tools.ide.editor.editor



import org.apache.http.ParseException
import org.eclipse.jface.text.Position
import org.eclipse.jface.text.source.Annotation
import org.eclipse.jface.text.source.ISourceViewer
import org.eclipse.jface.text.source.IVerticalRuler
import org.eclipse.jface.text.source.projection.ProjectionAnnotation
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel
import org.eclipse.jface.text.source.projection.ProjectionSupport
import org.eclipse.jface.text.source.projection.ProjectionViewer
import org.eclipse.swt.widgets.Composite
import org.eclipse.ui.editors.text.TextEditor
import org.eclipse.ui.views.contentoutline.IContentOutlinePage

import com.ariatemplates.tools.ide.backend.backend.Backend
import com.ariatemplates.tools.ide.backend.exception.BackendException
import com.ariatemplates.tools.ide.document.document.Document
import com.ariatemplates.tools.ide.document.provider.Provider
import com.ariatemplates.tools.ide.outline.outline.Outline
import com.google.gson.JsonSyntaxException
import com.ariatemplates.tools.ide.editor.configuration.view.source.Source



class Editor extends TextEditor {

	/***************************************************************************
	 * Building
	 **************************************************************************/

	Editor() {
		this.documentProvider = new Provider()
	}

	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		def viewer = new ProjectionViewer(parent, ruler, this.overviewRuler, this.isOverviewRulerVisible(), styles)
		this.getSourceViewerDecorationSupport viewer

		viewer
	}

	void createPartControl(Composite parent) {
		super.createPartControl parent

		// Folding -------------------------------------------------------------

		def viewer = this.sourceViewer

		this.projectionSupport = new ProjectionSupport(viewer, this.annotationAccess, this.sharedColors)
		this.projectionSupport.install()

		viewer.doOperation ProjectionViewer.TOGGLE
		this.annotationModel = viewer.projectionAnnotationModel
	}

	protected void initializeEditor() {
		super.initializeEditor()

		this.sourceViewerConfiguration = new Source()
	}

	def getAdapter(Class required) {
		// Outline -------------------------------------------------------------

		if (required == IContentOutlinePage.class) {
			if (this.contentOutlinePage == null) {
				def contentOutlinePage = new Outline()
				contentOutlinePage.input = this.editorInput
				this.contentOutlinePage = contentOutlinePage
			}

			return this.contentOutlinePage
		}

		// None ----------------------------------------------------------------

		super.getAdapter required
	}

	def getDocument() {
		this.documentProvider.getDocument this.editorInput
	}

	/***************************************************************************
	 * Folding
	 **************************************************************************/

	private annotationModel
	private projectionSupport

	private fold() {
		try {
			def document = this.document

			def result = Backend.get().service(document, "fold", [
				"0-based": true,
				"text": true,
				"length": true
			])

			def positions = result["ranges"].collect { range ->
				new Position(range["start"], range["length"])
			}

			this.updateFoldingStructure positions
		} catch (BackendException e) {
			e.printStackTrace()
		}
	}

	private oldAnnotations = []

	def updateFoldingStructure(positions) {
		// This will hold the new annotations along with their corresponding positions
		def newAnnotations = [:]

		def annotations = positions.collect { position ->
			def annotation = new ProjectionAnnotation()
			newAnnotations[annotation] = position
			annotation
		}

		this.annotationModel.modifyAnnotations this.oldAnnotations, newAnnotations, null

		this.oldAnnotations = annotations
	}

	/***************************************************************************
	 * Events
	 **************************************************************************/

	protected void editorSaved() {
		super.editorSaved()
		this.document.updateSource()
		this.update()
	}


	void update() {
		try {
			// this.format()
			// this.fold()
			// this.outline()
			this.validate()
		} catch (e) {
			e.printStackTrace()
		}
	}

	/***************************************************************************
	 * Formatting
	 **************************************************************************/

	// TODO Process input on initialization

	// private void format() {
	// 	def document = this.documentProvider.document this.editorInput

	// 	def formatted = Backend.get().rpc(document.mode, "format", ["source": document.get()])

	// 	document.set formatted["source"]
	// }


	/***************************************************************************
	 * Outline
	 **************************************************************************/

	private contentOutlinePage

	private outline() {
		def document = this.document

		try {
			def outline = Backend.get().service(document, "outline")
			this.contentOutlinePage.input = outline
		} catch (BackendException e) {
			e.printStackTrace()
		}
	}

	/***************************************************************************
	 * Validation
	 **************************************************************************/

	private validate() {
		def document = this.document
		document.clearMarkerAnnotations()

		try {
			def messages = Backend.get().service document, "validate"
			document.addAllMarkerAnnotations messages
		} catch (BackendException e) {
			e.printStackTrace()
		}
	}

}

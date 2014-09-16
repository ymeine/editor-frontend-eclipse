package com.ariatemplates.tools.ide.editor.editor;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.ParseException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.ariatemplates.tools.ide.backend.backend.Backend;
import com.ariatemplates.tools.ide.backend.exception.BackendException;
import com.ariatemplates.tools.ide.document.document.Document;
import com.ariatemplates.tools.ide.document.provider.Provider;
import com.ariatemplates.tools.ide.outline.outline.Outline;
import com.google.gson.JsonSyntaxException;
import com.ariatemplates.tools.ide.editor.configuration.view.source.Source;
import com.ariatemplates.tools.ide.editor.editor.Editor;



public class Editor extends TextEditor {

	/***************************************************************************
	 * Building
	 **************************************************************************/

	public Editor() {
		super();

		this.setDocumentProvider(new Provider());
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		ISourceViewer viewer = new ProjectionViewer(parent, ruler, this.getOverviewRuler(), this.isOverviewRulerVisible(), styles);
		this.getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// Folding -------------------------------------------------------------

		ProjectionViewer viewer = (ProjectionViewer) this.getSourceViewer();

		this.projectionSupport = new ProjectionSupport(viewer, this.getAnnotationAccess(), this.getSharedColors());
		this.projectionSupport.install();

		viewer.doOperation(ProjectionViewer.TOGGLE);
		this.annotationModel = viewer.getProjectionAnnotationModel();
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();

		this.setSourceViewerConfiguration(new Source());
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class required) {
		// Outline -------------------------------------------------------------

		if (required.equals(IContentOutlinePage.class)) {
			if (this.contentOutlinePage == null) {
				this.contentOutlinePage = new Outline();
				this.contentOutlinePage.setInput(this.getEditorInput());
			}

			return this.contentOutlinePage;
		}

		// None ----------------------------------------------------------------

		return super.getAdapter(required);
	}

	public Document getDocument() {
		return (Document) this.getDocumentProvider().getDocument(this.getEditorInput());
	}

	/***************************************************************************
	 * Folding
	 **************************************************************************/

	private static final String METHOD_FOLD = "fold";
	private static final String METHOD_VALIDATE = "validate";

	private static final String OPTION_ARG_0BASED = "0-based";
	private static final String OPTION_ARG_TEXT = "text";
	private static final String OPTION_ARG_LENGTH = "length";

	private static final String KEY_RANGES = "ranges";
	private static final String KEY_START = "start";
	private static final String KEY_LENGTH = "length";

	private ProjectionAnnotationModel annotationModel;
	private ProjectionSupport projectionSupport;

	@SuppressWarnings("unchecked")
	private void fold() throws JsonSyntaxException, ParseException, IOException {
		try {
			Document document = getDocument();
			Map<String, Object> arguments = new HashMap<String, Object>();
			arguments.put(Editor.OPTION_ARG_0BASED, true);
			arguments.put(Editor.OPTION_ARG_TEXT, true);
			arguments.put(Editor.OPTION_ARG_LENGTH, true);

			Map<String, Object> result = Backend.get().service(document, Editor.METHOD_FOLD, arguments);
			List<Map<String, Object>> folds = (List<Map<String, Object>>) result.get(Editor.KEY_RANGES);

			List<Position> positions = new ArrayList<Position>(folds.size());
			for (Map<String, Object> range : folds) {
				int start = ((Number) range.get(Editor.KEY_START)).intValue();
				int length = ((Number) range.get(Editor.KEY_LENGTH)).intValue();

				positions.add(new Position(start, length));
			}

			this.updateFoldingStructure(positions);
		} catch (BackendException e) {
			e.printStackTrace();
		}

	}

	private List<Annotation> oldAnnotations = new ArrayList<Annotation>();

	public void updateFoldingStructure(List<Position> positions) {
		// This will hold the new annotations along with their corresponding
		// positions
		HashMap<ProjectionAnnotation, Position> newAnnotations = new HashMap<ProjectionAnnotation, Position>();

		List<Annotation> annotations = new ArrayList<Annotation>();
		for (Position position : positions) {
			ProjectionAnnotation annotation = new ProjectionAnnotation();
			newAnnotations.put(annotation, position);
			annotations.add(annotation);
		}

		this.annotationModel.modifyAnnotations(this.oldAnnotations.toArray(new Annotation[this.oldAnnotations.size()]), newAnnotations, null);

		this.oldAnnotations = annotations;
	}

	/***************************************************************************
	 * Events
	 **************************************************************************/

	@Override
	protected void editorSaved() {
		super.editorSaved();
		this.getDocument().updateSource();
		this.update();
	}


	public void update() {
		try {
			// this.format();
			// this.fold();
			// this.outline();
			this.validate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * Formatting
	 **************************************************************************/

	// TODO Process input on initialization


	// private void format() throws IOException {
	// 	Document document = (Document) this.getDocument(this.getEditorInput());

	// 	Map<String, Object> argument = new HashMap<String, Object>();
	// 	argument.put("source", document.get());
	// 	Map<String, Object> formatted = Backend.get().rpc(document.getMode(), "format", argument);

	// 	document.set(formatted.get("source").toString());
	// }

	/***************************************************************************
	 * Outline
	 **************************************************************************/

	private static final String METHOD_OUTLINE = "outline";

	private Outline contentOutlinePage = null;

	private void outline() throws JsonSyntaxException, ParseException, IOException {
		Document document = this.getDocument();

		try {
			Map<String, Object> outline = Backend.get().service(document, Editor.METHOD_OUTLINE);
			this.contentOutlinePage.setInput(outline);
		} catch (BackendException e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * Validation
	 **************************************************************************/

	private void validate() throws IOException {
		Document document = getDocument();
		document.clearMarkerAnnotations();

		try {
			Map<String, Object> messages = Backend.get().service(document, Editor.METHOD_VALIDATE);
			document.addAllMarkerAnnotations(messages);
		} catch (BackendException e) {
			e.printStackTrace();
		}
	}


}

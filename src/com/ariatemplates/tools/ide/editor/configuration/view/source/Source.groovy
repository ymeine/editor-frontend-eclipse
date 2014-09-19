package com.ariatemplates.tools.ide.editor.configuration.view.source



import org.eclipse.jface.text.contentassist.IContentAssistant
import org.eclipse.jface.text.formatter.IContentFormatter
import org.eclipse.jface.text.presentation.IPresentationReconciler
import org.eclipse.jface.text.presentation.PresentationReconciler
import org.eclipse.jface.text.rules.DefaultDamagerRepairer
import org.eclipse.jface.text.source.IAnnotationHover
import org.eclipse.jface.text.source.ISourceViewer

import com.ariatemplates.tools.ide.document.partitioner.Partitioner
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner
import com.ariatemplates.tools.ide.editor.annotations.markers.hover.Hover



class Source extends org.eclipse.jface.text.source.SourceViewerConfiguration {
	// FIXME Should be taken from somewhere. But I don't see, as a mode is
	// related to a document, and here the viewer configuration is agnostic of
	// the document.
	// A solution would be to take a reference to the editor, that way we could
	// be able to request from it the document. Then the mode. But in that case,
	// there is no pre-fetch of the configuration data.

	// private static final mode = "html"

	// private configuration



	// private static final KEY_CONFIGURATION_WIDTH = "configuration"

	// public Source() {
	// 	super
	// 	try {
	// 		this.configuration = Backend.get().rpc(this.class.mode, this.class.KEY_CONFIGURATION_WIDTH)
	// 	} catch (e) {
	// 		e.printStackTrace()
	// 	}
	// }

	/***************************************************************************
	 * General configuration
	 **************************************************************************/

	// private static final KEY_TAB_WIDTH = "tabWidth"

	// @Override
	// int getTabWidth(ISourceViewer sourceViewer) {
	// 	if (this.configuration != null) {
	// 		return this.configuration[this.class.KEY_TAB_WIDTH]
	// 	}

	// 	super.getTabWidth sourceViewer
	// }

	/***************************************************************************
	 * Highlighting
	 **************************************************************************/

	IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		def reconciler = new PresentationReconciler()
		def dr = new DefaultDamagerRepairer(new SpecificRuleBasedScanner())
		reconciler.setDamager dr, Partitioner.PARTITION_NAME
		reconciler.setRepairer dr, Partitioner.PARTITION_NAME

		reconciler
	}

	/***************************************************************************
	 * Pending implementation
	 **************************************************************************/

	IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		super.getContentFormatter sourceViewer
	}

	IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		super.getContentAssistant sourceViewer
	}

	IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		new Hover()
	}
}

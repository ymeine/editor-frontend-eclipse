package com.ariatemplates.tools.ide.editor.configuration.view.source;

import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

import com.ariatemplates.tools.ide.document.partitioner.Partitioner;
import com.ariatemplates.tools.ide.modes.athtml.highlighting.SpecificRuleBasedScanner;
import com.ariatemplates.tools.ide.editor.annotations.markers.hover.Hover;

public class Source extends org.eclipse.jface.text.source.SourceViewerConfiguration {

	// FIXME Should be taken from somewhere. But I don't see, as a mode is
	// related to a document, and here the viewer configuration is agnostic of
	// the document.
	// A solution would be to take a reference to the editor, that way we could
	// be able to request from it the document. Then the mode. But in that case,
	// there is no pre-fetch of the configuration data.
	// private static final String mode = "html";
	//
	// private Map<String, Object> configuration = null;
	//
	// private static final String KEY_CONFIGURATION_WIDTH = "configuration";

	// public Source() {
	// super();
	// try {
	// configuration = Backend.get().rpc(mode,
	// Source.KEY_CONFIGURATION_WIDTH);
	// } catch (BackendException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/***************************************************************************
	 * General configuration
	 **************************************************************************/

	// private static final String KEY_TAB_WIDTH = "tabWidth";
	//
	// @Override
	// public int getTabWidth(ISourceViewer sourceViewer) {
	// if (configuration != null) {
	// return
	// ((Number)configuration.get(Source.KEY_TAB_WIDTH)).intValue();
	// }
	// return super.getTabWidth(sourceViewer);
	// }

	/***************************************************************************
	 * Highlighting
	 **************************************************************************/

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new SpecificRuleBasedScanner());
		reconciler.setDamager(dr, Partitioner.PARTITION_NAME);
		reconciler.setRepairer(dr, Partitioner.PARTITION_NAME);

		return reconciler;
	}

	/***************************************************************************
	 * Pending implementation
	 **************************************************************************/

	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		return super.getContentFormatter(sourceViewer);
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		return super.getContentAssistant(sourceViewer);
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new Hover();
	}

}

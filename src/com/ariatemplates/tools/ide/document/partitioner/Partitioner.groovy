package com.ariatemplates.tools.ide.document.partitioner



import org.eclipse.jface.text.DocumentEvent
import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.IDocumentPartitioner
import org.eclipse.jface.text.ITypedRegion
import org.eclipse.jface.text.TypedRegion



class Partitioner implements IDocumentPartitioner {
	static final PARTITION_NAME = "MAIN"

	private static final types = [this.PARTITION_NAME]

	private document
	private region



	Partitioner(document) {
		this.document = document
		this.region = new TypedRegion(0, this.document.length, this.class.PARTITION_NAME)
	}

	/***************************************************************************
	 * Partitioning
	 **************************************************************************/

	String[] getLegalContentTypes() { this.class.types }
	String getContentType(int offset) { this.class.PARTITION_NAME }

	ITypedRegion[] computePartitioning(int offset, int length) {
		this.region = new TypedRegion(0, this.document.length, this.class.PARTITION_NAME)
	}

	ITypedRegion getPartition(int offset) { this.region }

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	void connect(IDocument document) {}
	void disconnect() {}

	void documentAboutToBeChanged(DocumentEvent event) {
		this.region = new TypedRegion(0, this.document.length, this.class.PARTITION_NAME);
	}

	boolean documentChanged(DocumentEvent event) { false }
}

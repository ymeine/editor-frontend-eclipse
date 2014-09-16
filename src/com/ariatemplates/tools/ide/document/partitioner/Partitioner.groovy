package com.ariatemplates.tools.ide.document.partitioner



import org.eclipse.jface.text.DocumentEvent
import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.IDocumentPartitioner
import org.eclipse.jface.text.ITypedRegion
import org.eclipse.jface.text.TypedRegion

import com.ariatemplates.tools.ide.document.document.Document



class Partitioner implements IDocumentPartitioner {
	static final PARTITION_NAME = "MAIN"
	private static final types = [this.PARTITION_NAME]

	private Document document
	private ITypedRegion region

	def Partitioner(Document document) {
		this.document = document
		this.region = new TypedRegion(0, this.document.getLength(), this.class.PARTITION_NAME)
	}

	/***************************************************************************
	 * Partitioning
	 **************************************************************************/

	String[] getLegalContentTypes() { Partitioner.types }
	String getContentType(int offset) { this.class.PARTITION_NAME }

	ITypedRegion[] computePartitioning(int offset, int length) {
		this.region = new TypedRegion(0, this.document.getLength(), this.class.PARTITION_NAME);
		return [this.region];
	}

	ITypedRegion getPartition(int offset) { this.region }

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	void connect(IDocument document) {}
	void disconnect() {}

	void documentAboutToBeChanged(DocumentEvent event) {
		this.region = new TypedRegion(0, this.document.getLength(), this.class.PARTITION_NAME);
	}

	boolean documentChanged(DocumentEvent event) { false }
}

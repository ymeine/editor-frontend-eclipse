package com.ariatemplates.tools.ide.document.partitioner;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;

import com.ariatemplates.tools.ide.document.document.Document;
import com.ariatemplates.tools.ide.document.partitioner.Partitioner;

public class Partitioner implements IDocumentPartitioner {

	public static final String PARTITION_NAME = "MAIN";

	private static final String[] types = { Partitioner.PARTITION_NAME };

	private Document document = null;

	private ITypedRegion region = null;

	public Partitioner(Document document) {
		this.document = document;
		this.region = new TypedRegion(0, this.document.getLength(), Partitioner.PARTITION_NAME);
	}

	/***************************************************************************
	 * Partitioning
	 **************************************************************************/

	@Override
	public String[] getLegalContentTypes() {
		return Partitioner.types;
	}

	@Override
	public String getContentType(int offset) {
		return Partitioner.PARTITION_NAME;
	}

	@Override
	public ITypedRegion[] computePartitioning(int offset, int length) {
		this.region = new TypedRegion(0, this.document.getLength(), Partitioner.PARTITION_NAME);
		ITypedRegion[] regions = { this.region };
		return regions;
	}

	@Override
	public ITypedRegion getPartition(int offset) {
		return this.region;
	}

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	@Override public void connect(IDocument document) {}
	@Override public void disconnect() {}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		this.region = new TypedRegion(0, this.document.getLength(), Partitioner.PARTITION_NAME);
	}

	@Override
	public boolean documentChanged(DocumentEvent event) {
		return false;
	}
}

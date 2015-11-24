package net.fischboeck.mosaique.ui.tasks;

import javafx.concurrent.Task;
import net.fischboeck.mosaique.MosaiqueBuilder;

public class ImageMapBuilderTaskDelegate extends Task<String[][]> {

	private MosaiqueBuilder		mb;
	
	public ImageMapBuilderTaskDelegate(MosaiqueBuilder mb) {
		this.mb = mb;
	}
	
	@Override
	protected String[][] call() throws Exception {
		return mb.createImageMap();
	}
}

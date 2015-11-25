package net.fischboeck.mosaique;

import java.awt.image.BufferedImage;

public interface ProgressCallback {
	
	public void onTileCalculated(String path, BufferedImage img);
	
	public void onImageComplete();
}

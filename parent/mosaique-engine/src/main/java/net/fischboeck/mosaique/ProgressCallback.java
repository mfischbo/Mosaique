package net.fischboeck.mosaique;

import java.awt.image.BufferedImage;

public interface ProgressCallback {
	
	public void onMosaiqueCalculated(String path, BufferedImage img);
}

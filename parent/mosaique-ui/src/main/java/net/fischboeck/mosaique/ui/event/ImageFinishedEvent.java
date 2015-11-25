package net.fischboeck.mosaique.ui.event;

import java.awt.image.BufferedImage;

public class ImageFinishedEvent {

	private BufferedImage	image;
	
	public ImageFinishedEvent(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage getImage() {
		return this.image;
	}
}

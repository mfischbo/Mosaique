package net.fischboeck.mosaique.ui.event;

import java.awt.image.BufferedImage;

public class TileCalculatedEvent {

	private String path;
	private BufferedImage img;
	
	public TileCalculatedEvent(String path, BufferedImage image) {
		this.path = path;
		this.img = image;
	}

	public String getPath() {
		return path;
	}

	public BufferedImage getImg() {
		return img;
	}
}

package net.fischboeck.mosaique.db;

import java.time.LocalDateTime;
import java.util.List;

public class ImageCollection {

	private String				id;
	private String 				name;
	private LocalDateTime		dateCreated;
	private List<ImageDBEntry>	images;

	public ImageCollection() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public List<ImageDBEntry> getImages() {
		return images;
	}

	public void setImages(List<ImageDBEntry> images) {
		this.images = images;
	}
}

package net.fischboeck.mosaique.ui.tasks;

import java.awt.image.BufferedImage;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;

import javafx.concurrent.Task;
import net.fischboeck.mosaique.MosaiqueBuilder;
import net.fischboeck.mosaique.ProgressCallback;
import net.fischboeck.mosaique.ui.event.TileCalculatedEvent;

public class MosaiqueBuilderTaskDelegate extends Task<Map<String, BufferedImage>> implements ProgressCallback {

	private ApplicationEventPublisher	_publisher;
	private MosaiqueBuilder 			_builder;
	
	public MosaiqueBuilderTaskDelegate(ApplicationEventPublisher publisher, MosaiqueBuilder b) {
		this._builder = b;
		this._publisher = publisher;
		_builder.registerProgressCallback(this);
	}
	
	@Override
	protected Map<String, BufferedImage> call() throws Exception {
		return _builder.create();
	}

	@Override
	public void onMosaiqueCalculated(String path, BufferedImage img) {
		_publisher.publishEvent(new TileCalculatedEvent(path, img));
	}
}
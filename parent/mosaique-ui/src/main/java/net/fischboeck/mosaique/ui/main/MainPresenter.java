package net.fischboeck.mosaique.ui.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import net.fischboeck.mosaique.MasterImage;
import net.fischboeck.mosaique.MosaiqueBuilder;
import net.fischboeck.mosaique.db.ImageDB;
import net.fischboeck.mosaique.ui.event.ViewDisposedEvent;
import net.fischboeck.mosaique.ui.imagedb.ImagedbView;

@Component
public class MainPresenter implements Initializable {

	@Autowired
	private ImagedbView				imdbView;
	
	@Autowired
	private WizardView				wizView;

	@FXML private AnchorPane		masterPane;
	
	public void onFileNewClicked() {
		System.out.println("u clicked something");
	}
	
	@EventListener
	public void handleViewDisposedEvent(ViewDisposedEvent<String> event) {
		this.masterPane.getChildren().clear();
	}
	
	@EventListener
	public void handleOtherViewDisposedEvent(ViewDisposedEvent<BuilderConfiguration> event) {
		this.masterPane.getChildren().clear();
		BuilderConfiguration c = event.getObject();
	
		ImageDB db = new ImageDB();
		c.collections.forEach(col -> {
			col.getImages().forEach(i -> {
				db.addImage(i);
			});
		});
	
		try {
			MasterImage im = new MasterImage(c.masterImage, c.tileCount);
			MosaiqueBuilder builder = new MosaiqueBuilder(db, im, 
					c.allowImageReuse, c.useFormatFilter, c.mode, c.strayFactor, c.useSubsampling,
					c.resultWidth, c.resultHeight);
			
			String[][] map = builder.createImageMap();
			System.out.println("Looks good");
		} catch (Exception ex) {
			
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void onMosaiqueNewClicked() {
		Node n = wizView.getView();
		this.masterPane.getChildren().clear();
		this.masterPane.getChildren().add(n);
	}
	
	public void onImageDBNewClicked() {
		
		Node n = imdbView.getView();
		AnchorPane.setTopAnchor(n, 0.0);
		AnchorPane.setLeftAnchor(n, 0.0);
		AnchorPane.setRightAnchor(n, 0.0d);
		AnchorPane.setBottomAnchor(n, 0.0d);
		
		this.masterPane.getChildren().clear();
		this.masterPane.getChildren().add(imdbView.getView());
	}
}

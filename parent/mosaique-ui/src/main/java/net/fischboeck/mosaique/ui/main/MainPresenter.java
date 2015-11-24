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
	public void handleViewDisposedEvent(ViewDisposedEvent event) {
			this.masterPane.getChildren().clear();
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

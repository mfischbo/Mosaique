package net.fischboeck.mosaique.ui.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import net.fischboeck.mosaique.ui.imagedb.ImagedbView;

@Component
public class MainPresenter implements Initializable {

	@Autowired
	private ImagedbView				imdbView;

	@Autowired
	private MainView				mainView;
	
	@FXML private AnchorPane		masterPane;
	
	public void onFileNewClicked() {
		System.out.println("u clicked something");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
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

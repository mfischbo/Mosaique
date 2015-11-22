package net.fischboeck.mosaique.ui.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

@Component
public class MainPresenter implements Initializable {
	
	@FXML Button		zoomOut;
	@FXML Button		zoomIn;
	
	public void onFileNewClicked() {
		System.out.println("u clicked something");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}

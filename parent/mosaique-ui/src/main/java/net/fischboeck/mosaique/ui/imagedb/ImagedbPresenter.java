package net.fischboeck.mosaique.ui.imagedb;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.fischboeck.mosaique.ui.AppBase;

@Component
public class ImagedbPresenter implements Initializable {

	@Autowired
	private AppBase			_base;
	
	@FXML private ScrollPane contentPane;
	
	final FileChooser      fCh = new FileChooser();
	final DirectoryChooser dCh = new DirectoryChooser();
	
	public void onAddDirectoryClicked() {
		File f = dCh.showDialog(_base.getScene().getWindow());
		System.out.println("Choosen directory " + f.getAbsolutePath());
	}
	
	public void onAddFileClicked() {
		List<File> files = fCh.showOpenMultipleDialog(_base.getScene().getWindow());
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		System.out.println("Setting drop handler");
		
		contentPane.setOnDragOver(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			}
		});
		
		contentPane.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					for (File f : db.getFiles()) {
						System.out.println(f.getAbsolutePath());
					}
				}
				
				event.setDropCompleted(true);
				event.consume();
			}
		});
	}
}

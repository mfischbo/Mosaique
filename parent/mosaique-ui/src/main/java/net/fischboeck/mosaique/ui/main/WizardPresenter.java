package net.fischboeck.mosaique.ui.main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.fischboeck.mosaique.db.FileCollector;
import net.fischboeck.mosaique.ui.AppBase;
import net.fischboeck.mosaique.ui.event.ViewDisposedEvent;

@Component
public class WizardPresenter implements Initializable {

	@Autowired
	private ApplicationEventPublisher		_publisher;

	@Autowired
	private AppBase							_base;
	
	private File							_masterImage;
	
	@FXML	private Slider					tileSlider;
	@FXML	private Slider					straySlider;
	@FXML	private Label					lblTiles;
	@FXML	private Label					lblStray;
	@FXML	private ChoiceBox<String>		modeSelect;
	@FXML	private ImageView				imageView;
	@FXML	private TextField				txOutputWidth;
	@FXML	private TextField				txOutputHeight;
	@FXML	private ListView<String>		collectionView;
	
	private FileChooser						_fCh = new FileChooser();

	public void onChooseFileButtonClicked() {
		File f = _fCh.showOpenDialog(_base.getScene().getWindow());
		setMasterFile(f);
	}

	private void setMasterFile(File f) {
		if (f != null && f.isFile()) {
			Image i = new Image("file:" + f.getAbsolutePath());
			imageView.setImage(i);
			
			txOutputWidth.setText("" + (int) i.getWidth());
			txOutputHeight.setText("" + (int) i.getHeight());
			
			_masterImage = f;
		}
	}
	
	public void onCancelButtonClicked() {
		_publisher.publishEvent(new ViewDisposedEvent());
	}
	
	public void onCreateButtonClicked() {
	
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		lblTiles.textProperty().bind(tileSlider.valueProperty().asString());
		lblStray.textProperty().bind(straySlider.valueProperty().asString());
	
		modeSelect.setItems(FXCollections.observableArrayList("Color", "Greyscale"));
		modeSelect.setValue("Color");
		
		_fCh.getExtensionFilters().add(new ExtensionFilter("Images", FileCollector.extensions));
		
		// dnd on imageview
		imageView.setOnDragOver(new EventHandler<DragEvent>() {

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
		
		imageView.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					for (File f : db.getFiles()) {
						if (f.isFile()) {
							setMasterFile(f);
						}
					}
				}
				event.setDropCompleted(true);
				event.consume();
			}
		});
	}
}
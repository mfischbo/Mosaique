package net.fischboeck.mosaique.ui.main;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.fischboeck.mosaique.MosaiqueBuilder.Mode;
import net.fischboeck.mosaique.db.FileCollector;
import net.fischboeck.mosaique.db.ImageCollection;
import net.fischboeck.mosaique.ui.AppBase;
import net.fischboeck.mosaique.ui.ImageCollectionService;
import net.fischboeck.mosaique.ui.event.ViewDisposedEvent;
import net.fischboeck.mosaique.ui.event.WizardFinishedEvent;

@Component
public class WizardPresenter implements Initializable {

	@Autowired
	private ApplicationEventPublisher		_publisher;

	@Autowired
	private AppBase							_base;
	
	@Autowired
	private ImageCollectionService			_service;
	
	private Logger _log = LoggerFactory.getLogger(getClass());
	
	// model classes
	private File							_masterImage;
	private ObservableList<String>			_imageCollections;
	
	@FXML	private Slider					tileSlider;
	@FXML	private Slider					straySlider;
	@FXML	private Label					lblTiles;
	@FXML	private Label					lblStray;
	@FXML	private ChoiceBox<String>		modeSelect;
	@FXML	private ImageView				imageView;
	@FXML	private TextField				txOutputWidth;
	@FXML	private TextField				txOutputHeight;
	@FXML	private ListView<String>		collectionView;
	@FXML	private CheckBox				allowReuse;
	@FXML	private CheckBox				useFormatFilter;
	@FXML	private CheckBox				useSubsampling;
	
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
		_publisher.publishEvent(new ViewDisposedEvent<String>(null));
	}
	
	public void onCreateButtonClicked() {
		
		BuilderConfiguration b = new BuilderConfiguration();
		b.allowImageReuse =	allowReuse.isSelected();
		b.masterImage = _masterImage;
		b.resultWidth = Integer.parseInt(txOutputWidth.getText());
		b.resultHeight = Integer.parseInt(txOutputHeight.getText());
		b.strayFactor  = (int) (straySlider.getValue());
		b.useFormatFilter = useFormatFilter.isSelected();
		b.useSubsampling  = useSubsampling.isSelected();
		b.tileCount = (int) tileSlider.getValue();
		
		b.mode = Mode.BRIGHTNESS;
		if (modeSelect.getValue().equals("Color"))
			b.mode = Mode.COLOR;
		
		List<ImageCollection> x = _service.getAllCollections();
		b.collections = x.stream().filter(p -> _imageCollections.contains(p.getName())).collect(Collectors.toList());
		
		_publisher.publishEvent(new WizardFinishedEvent(b));
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
		
		List<ImageCollection> col = _service.getAllCollections();
		ObservableList<String> items = FXCollections.observableArrayList();
		col.forEach(c -> {
			items.add(c.getName());
		});
		collectionView.setItems(items);

		collectionView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		collectionView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_imageCollections = collectionView.getSelectionModel().getSelectedItems();
				_log.info("Selection contains {} items", _imageCollections.size());
			}
		});
	}
}
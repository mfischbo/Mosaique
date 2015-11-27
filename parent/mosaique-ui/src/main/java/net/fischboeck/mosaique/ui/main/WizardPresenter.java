package net.fischboeck.mosaique.ui.main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.util.StringConverter;
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
	private ObservableList<ImageCollection> _imageCollections;
	
	private double							_aspectRatio;

	@FXML	private HBox					container;
	@FXML	private Slider					tileSlider;
	@FXML	private Slider					straySlider;
	@FXML	private Label					lblTiles;
	@FXML	private Label					lblStray;
	@FXML	private ChoiceBox<String>		modeSelect;
	@FXML	private ImageView				imageView;
	@FXML	private TextField				txOutputWidth;
	@FXML	private TextField				txOutputHeight;
	@FXML	private ListView<ImageCollection>		collectionView;
	@FXML	private CheckBox				allowReuse;
	@FXML	private CheckBox				useFormatFilter;
	@FXML	private CheckBox				useSubsampling;
	@FXML	private CheckBox				keepRatio;
	
	private FileChooser						_fCh = new FileChooser();
	
	public void onChooseFileButtonClicked() {
		File f = _fCh.showOpenDialog(_base.getScene().getWindow());
		setMasterFile(f);
	}

	private void setMasterFile(File f) {
		if (f != null && f.isFile()) {
			Image i = new Image("file:" + f.getAbsolutePath());
			imageView.setImage(i);
			_aspectRatio = i.getWidth() / i.getHeight();
			
			txOutputWidth.setText("" + (int) i.getWidth());
			txOutputHeight.setText("" + (int) i.getHeight());
			
			_masterImage = f;
		}
	}
	
	public void onCancelButtonClicked() {
		_publisher.publishEvent(new ViewDisposedEvent());
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
		
		b.collections = collectionView.getSelectionModel().getSelectedItems();
		_publisher.publishEvent(new WizardFinishedEvent(b));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// update the image collection when the view becomes active
		container.parentProperty().addListener(new ChangeListener<Parent>() {

			@Override
			public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
				if (newValue != null) {
					collectionView.setItems(FXCollections.observableArrayList(_service.getAllCollections()));
				}
			}
		});
		
		// placeholder image for dropdown area
		this.imageView.setImage(new Image("placeholder.png"));
		
		// binding for changing the output sizes
		txOutputWidth.textProperty().bindBidirectional(txOutputHeight.textProperty(), new StringConverter<String>() {

			@Override
			public String toString(String object) {
				if (!keepRatio.isSelected()) return txOutputWidth.getText();
				try {
					int val = Integer.parseInt(object);
					return Integer.toString((int) (val*_aspectRatio));
				} catch (Exception ex) {
					return "0";
				}
			}

			@Override
			public String fromString(String string) {
				if (!keepRatio.isSelected()) return txOutputHeight.getText();
				try {
					int val = Integer.parseInt(string);
					return Integer.toString((int) (val* (1 / _aspectRatio)));
				} catch (Exception ex) {
					return "0";
				}
			}
		});
				
		lblTiles.textProperty().bind(tileSlider.valueProperty().asString("%.0f"));
		lblStray.textProperty().bind(straySlider.valueProperty().asString("%.0f"));
		
		tileSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				int sum = _imageCollections.stream().mapToInt(ImageCollection::getImageCount).sum();
				if (sum >= Math.pow(newValue.doubleValue(), 2))
					allowReuse.setDisable(false);
			}
		});
	
		modeSelect.setItems(FXCollections.observableArrayList("Color", "Greyscale"));
		modeSelect.setValue("Color");
		
		_fCh.setInitialDirectory(new File(System.getProperty("user.home")));
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
	
		collectionView.setCellFactory(new Callback<ListView<ImageCollection>, ListCell<ImageCollection>>() {

			@Override
			public ListCell<ImageCollection> call(ListView<ImageCollection> param) {
			
				ListCell<ImageCollection> c = new ListCell<ImageCollection>() {
					
					@Override
					protected void updateItem(ImageCollection col, boolean empty) {
						super.updateItem(col, empty);
						if (col != null)
							setText(col.getName() + " (" + col.getImageCount() + ")");
						else
							setText("");
					}
				};
				return c;
			}
			
		});
		collectionView.setItems(FXCollections.observableArrayList(_service.getAllCollections()));
		collectionView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		collectionView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ImageCollection>() {

			@Override
			public void changed(ObservableValue<? extends ImageCollection> observable, ImageCollection oldValue, ImageCollection newValue) {
				_imageCollections = collectionView.getSelectionModel().getSelectedItems();
				_log.info("Selection contains {} items", _imageCollections.size());
				
				int sum = _imageCollections.stream().mapToInt(ImageCollection::getImageCount).sum();
				_log.info("Comparing {} to {}", sum, Math.pow(tileSlider.getValue(), 2));
				if (sum > Math.pow(tileSlider.getValue(), 2)) 
					allowReuse.setDisable(false);
			}
		});
	}
}
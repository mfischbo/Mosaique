package net.fischboeck.mosaique.ui.imagedb;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.fischboeck.mosaique.analyzer.Result;
import net.fischboeck.mosaique.db.FileCollector;
import net.fischboeck.mosaique.db.ImageCollection;
import net.fischboeck.mosaique.db.ImageDBReader;
import net.fischboeck.mosaique.ui.AppBase;
import net.fischboeck.mosaique.ui.event.ViewDisposedEvent;

@Component
public class ImagedbPresenter implements Initializable {

	@Autowired
	private AppBase					_base;
	
	@Autowired
	private ApplicationEventPublisher	_publisher;
	
	@FXML private TextField			nameField;
	@FXML private ScrollPane 		contentPane;
	@FXML private FlowPane   		flowPane;

	private List<File>				_directories = new LinkedList<>();
	private List<File>				_files = new LinkedList<>();
	
	
	private final FileChooser      	_fCh = new FileChooser();
	private final DirectoryChooser  _dCh = new DirectoryChooser();
	
	public void onAddDirectoryClicked() {
		File f = _dCh.showDialog(_base.getScene().getWindow());
		if (f != null && f.isDirectory()) {
			addDirectory(f);
		}
	}
	
	public void onAddFileClicked() {
		List<File> files = _fCh.showOpenMultipleDialog(_base.getScene().getWindow());
		if (files != null) { 
			for (File f : files)
				addFile(f);
		}
	}
	
	public void onCancelClicked() {
		// remove all icon panes
		this.flowPane.getChildren().clear();
		this._directories.clear();
		this._files.clear();
		this.nameField.setText("");
		_publisher.publishEvent(new ViewDisposedEvent());
	}
	
	public void onSaveClicked() {

		FileCollector fc = new FileCollector(_files);
		List<File> files = fc.getResult();
		
		ProgressDialog pd = new ProgressDialog("Importing images...");
		Task<List<Result>> task = new ResultSetBuilderTask(files);
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				
				ImageCollection c = new ImageCollection();
				c.setName(nameField.getText());
				c.setImages(task.getValue());
			
				try {
					File dst = new File(System.getProperty("user.home") + "/.cache/mosaique/" + c.getId() + ".json");
					ImageDBReader.serialize(c, dst);
					onCancelClicked();
				} catch (Exception ex) {
					
				}
				pd.close();
			}
		});
		
		pd.show();
		pd.getProgressBar().progressProperty().bind(task.progressProperty());
		Thread th = new Thread(task);
		th.start();
	}

	
	private void addDirectory(File f) {
		Image i = new Image("folder.png", 50, 50, true, true);
		IconPane p = new IconPane(i, f.getName());
		flowPane.getChildren().add(p);
		_files.add(f);
	}
	
	private void addFile(File f) {
		String[] t = f.getName().split("\\.");
		if (FileCollector.fileTypes.contains(t[t.length -1])) {
			Image i = new Image("file:" + f.getAbsolutePath(), 50, 50, true, true);
			IconPane p = new IconPane(i, f.getName());
			flowPane.getChildren().add(p);
			_files.add(f);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		File f = new File(System.getProperty("user.home"));
		_fCh.setInitialDirectory(f);
		_dCh.setInitialDirectory(f);
		
		flowPane.setPrefWidth(contentPane.getWidth());
		flowPane.setPrefHeight(contentPane.getHeight());
		flowPane.setPrefWrapLength(contentPane.getWidth());
		
		flowPane.setOnDragOver(new EventHandler<DragEvent>() {

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
		
		flowPane.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					for (File f : db.getFiles()) {
						if (f.isDirectory()) 
							addDirectory(f);
						
						if (f.isFile()) 
							addFile(f);
					}
				}
				event.setDropCompleted(true);
				event.consume();
			}
		});
		
		contentPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				flowPane.setPrefWidth(newValue.getWidth());
				flowPane.setPrefHeight(newValue.getHeight());
				flowPane.setPrefWrapLength(newValue.getWidth());
			}
		});
	}
}

package net.fischboeck.mosaique.ui.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import net.fischboeck.mosaique.MasterImage;
import net.fischboeck.mosaique.MosaiqueBuilder;
import net.fischboeck.mosaique.db.ImageDB;
import net.fischboeck.mosaique.ui.AppBase;
import net.fischboeck.mosaique.ui.event.ImageFinishedEvent;
import net.fischboeck.mosaique.ui.event.TileCalculatedEvent;
import net.fischboeck.mosaique.ui.event.ViewDisposedEvent;
import net.fischboeck.mosaique.ui.event.WizardFinishedEvent;
import net.fischboeck.mosaique.ui.imagedb.ImagedbView;
import net.fischboeck.mosaique.ui.tasks.MosaiqueBuilderTaskDelegate;

@Component
public class MainPresenter implements Initializable {

	@Autowired
	private ImagedbView				imdbView;
	
	@Autowired
	private WizardView				wizView;
	
	@Autowired
	private ApplicationEventPublisher	publisher;
	
	@Autowired
	private AppBase					_base;
	

	@FXML private AnchorPane		masterPane;
	@FXML private MenuItem			exportItem;
	
	
	private String[][]				imageMap;
	private BufferedImage			finalResult;
	private Canvas 					canvas;
	private int						callCount = 0;
	private long					memUsed;

	private Logger log = LoggerFactory.getLogger(getClass());

	@EventListener
	public void handleViewDisposedEvent(ViewDisposedEvent<String> event) {
		this.masterPane.getChildren().clear();
	}
	
	@EventListener
	public void handleOtherViewDisposedEvent(WizardFinishedEvent event) {
		finalResult = null;
		exportItem.setDisable(true);
		
		this.masterPane.getChildren().clear();
		this.memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		BuilderConfiguration c = event.getConfiguration();
	
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
			
			this.imageMap = builder.createImageMap();
		
			// create the canvas
			this.canvas = new Canvas();
			this.canvas.setWidth(c.resultWidth);
			this.canvas.setHeight(c.resultHeight);
			this.masterPane.getChildren().add(this.canvas);
			
			MosaiqueBuilderTaskDelegate dt = new MosaiqueBuilderTaskDelegate(publisher, builder);
			Thread q = new Thread(dt);
			q.setDaemon(true);
			q.start();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	@EventListener
	public void handleTileCreatedEvent(TileCalculatedEvent event) {
		try {
			callCount++;
			Image i = SwingFXUtils.toFXImage(event.getImg(), null);
			
			for (int x = 0; x < this.imageMap[0].length; ++x) {
				for (int y = 0; y < this.imageMap[0].length; ++y) {
					
					if (imageMap[x][y].equals(event.getPath())) {
						this.canvas.getGraphicsContext2D().drawImage(i, x * i.getWidth(), y * i.getHeight());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@EventListener
	public void handleImageFinishedResult(ImageFinishedEvent event) {
		log.info("Received image finished event. TileCreat has been called {} times", callCount);
		finalResult = event.getImage();
		exportItem.setDisable(false);
	
		try {
			Image i = SwingFXUtils.toFXImage(event.getImage(), null);
			this.canvas.getGraphicsContext2D().drawImage(i, 0, 0);
			
			long usedNow = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			log.info("Generation took {} mb of RAM", (usedNow - memUsed) / 1024 / 1024);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		exportItem.setDisable(true);
	}
	
	public void onMosaiqueNewClicked() {
		Node n = wizView.getView();
		AnchorPane.setTopAnchor(n, 0.0);
		AnchorPane.setLeftAnchor(n, 0.0);
		AnchorPane.setRightAnchor(n, 0.0);
		AnchorPane.setBottomAnchor(n, 0.0);
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
	
	public void onExportAsClicked() {
		FileChooser fch = new FileChooser();
		File f = fch.showSaveDialog(_base.getScene().getWindow());
		if (f != null) {
			try {
				ImageIO.write(finalResult, "PNG", f);
				
			} catch (Exception ex) {
				log.error("Failed writing final image to file {}", f.getAbsolutePath());
			}
		}
	}
	
	public void onCloseClicked() {
		Platform.exit();
		System.exit(0);
	}
}
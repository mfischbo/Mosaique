package net.fischboeck.mosaique.ui.imagedb;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class IconPane extends Pane {

	private VBox		_vbox;
	private ImageView	_image;
	private Label		_lblTitle;
	
	private boolean		_isMarked = false;
	
	public IconPane(Image image, String title) {
		super();
	
		this.setPadding(new Insets(8));
		
		_lblTitle = new Label();
		_lblTitle.setText(title);
		_lblTitle.setMaxWidth(68.d);
	
		_image = new ImageView();
		_image.setImage(image);
	
		_vbox = new VBox();
		
		this.getChildren().add(_vbox);
		_vbox.getChildren().add(_image);
		_vbox.getChildren().add(_lblTitle);
	
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) {
					IconPane pane = (IconPane) event.getSource();
					pane.setMarked();
				}
			}
		});
	}
	
	public void setMarked() {
		if (!_isMarked)
			this.setStyle("-fx-background-color: #aaaaaa;");
		else
			this.setStyle("-fx-background-color: transparent;");
		
		_isMarked = !_isMarked;
	}
}

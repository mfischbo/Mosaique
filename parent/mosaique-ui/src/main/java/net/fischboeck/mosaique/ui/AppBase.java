package net.fischboeck.mosaique.ui;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.Scene;
import net.fischboeck.mosaique.ui.main.MainView;

@Component
public class AppBase {

	@Autowired MainView		_view;
	private Scene			_scene;
	
	public static List<String> fileTypes = new LinkedList<>();
	
	static {
		fileTypes.add("jpg");
		fileTypes.add("jpeg");
		fileTypes.add("png");
		fileTypes.add("tiff");
	}
	
	public void setScene(Scene scene) {
		this._scene = scene;
	}
	
	public Scene getScene() {
		return this._scene;
	}
	
	public void showMainView() {
		_scene.setRoot(_view.getView());
	}
}

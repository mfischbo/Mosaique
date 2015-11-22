package net.fischboeck.mosaique.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.Scene;
import net.fischboeck.mosaique.ui.main.MainView;

@Component
public class AppBase {

	@Autowired MainView		_view;
	private Scene			_scene;
	
	
	public void setScene(Scene scene) {
		this._scene = scene;
	}
	
	public void showMainView() {
		_scene.setRoot(_view.getView());
	}
}

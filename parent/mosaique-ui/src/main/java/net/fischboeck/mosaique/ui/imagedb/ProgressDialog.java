package net.fischboeck.mosaique.ui.imagedb;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;

public class ProgressDialog extends Alert {

	private ProgressBar		_pBar;

	
	public ProgressDialog(String title) {
		super(AlertType.INFORMATION);
		this.setTitle(title);
		this.setHeaderText("Your image collection is being created. This might take a while");
	
		this.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
		this._pBar = new ProgressBar();
		this.getDialogPane().setContent(_pBar);
	}
	
	public ProgressBar getProgressBar() {
		return _pBar;
	}
}

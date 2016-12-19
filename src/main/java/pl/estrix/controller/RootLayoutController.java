package pl.estrix.controller;

import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import pl.estrix.app.MainApp;

public class RootLayoutController {
	
	static Logger LOG = Logger.getLogger(RootLayoutController.class);

	private MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void handleAbout() {
		boolean okClicked = mainApp.showAboutDialog();
		if (okClicked) {			
		}
	}
	


	@FXML
	private void handleExit() {
		mainApp.closeApp();
	}
}
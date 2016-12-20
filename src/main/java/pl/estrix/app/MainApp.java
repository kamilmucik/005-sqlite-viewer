package pl.estrix.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.estrix.controller.AboutDialogController;
import pl.estrix.controller.MainOverviewController;
import pl.estrix.controller.RootLayoutController;
import pl.estrix.model.SQLQuery;
import pl.estrix.persist.SQLLiteQueryBean;
import pl.estrix.persist.SQLiteBean;
import pl.estrix.util.SQLiteUtil;

public class MainApp extends Application {

	static Logger LOG = Logger.getLogger(MainApp.class);

	private Stage primaryStage;
	private BorderPane rootLayout;
	private Image icon;

	private ObservableList<SQLQuery> queriesData = FXCollections.observableArrayList();

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("SQLite Viewer");
		icon = new Image(MainApp.class.getResourceAsStream("/images/Address_Book.png"));
		this.primaryStage.getIcons().add(icon);
		primaryStage.resizableProperty().setValue(Boolean.FALSE);
		primaryStage.setResizable(false);

		try {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);

			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);

			primaryStage.show();
			primaryStage.setOnCloseRequest(we -> closeApp());
		} catch (IOException e) {
			LOG.error(e);
		}

		showPersonOverview();

		Path filePath = Paths.get(".baza.db");
		boolean fileExists = Files.exists(filePath, LinkOption.NOFOLLOW_LINKS);
		LOG.debug("fileExists: " + fileExists + " : " + filePath.toAbsolutePath());
		if (fileExists == false) {
			boolean isCreated = SQLiteBean.createTable("");
			LOG.debug("isCreated: " + isCreated);
		} else {
			List<SQLQuery> queryList = SQLLiteQueryBean.getAll();
			if (queryList != null)
				Collections.sort(queryList);
//			queriesData.clear();
//			queriesData.addAll(queryList);
		}

	}

	public MainApp() {

	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showPersonOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/MainOverview.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();

			MainOverviewController controller = loader.getController();
			controller.setMainApp(this);

			// Set person overview into the center of root layout.
			rootLayout.setCenter(personOverview);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeApp() {
		((javafx.stage.Stage) this.getPrimaryStage().getScene().getWindow()).close();
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}


	public boolean showAboutDialog() {
		try {
			// Load the fxml file and create a new stage for the popup
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/AboutDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("O programie");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.initOwner(primaryStage);
			dialogStage.getIcons().add(icon);
			dialogStage.resizableProperty().setValue(Boolean.FALSE);
			dialogStage.setResizable(false);
			
			dialogStage.setWidth(360);
			dialogStage.setHeight(234);

			// http://stackoverflow.com/questions/13702191/center-location-of-stage
			dialogStage.setX((primaryStage.getX() + (primaryStage.getWidth() / 2)) - (dialogStage.getWidth() / 2));
			dialogStage.setY((primaryStage.getY() + (primaryStage.getHeight() / 2)) - (dialogStage.getHeight() / 2));			

			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller
			AboutDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();

		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}

	public BorderPane getRootLayout() {
		return rootLayout;
	}

	public void setRootLayout(BorderPane rootLayout) {
		this.rootLayout = rootLayout;
	}

	public ObservableList<SQLQuery> getQueriesData() {
		queriesData.clear();

		List items =SQLLiteQueryBean.getAll();
		if (items != null)
			queriesData.addAll(items);
		return queriesData;
	}

}

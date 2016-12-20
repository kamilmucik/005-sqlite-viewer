package pl.estrix.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import pl.estrix.app.MainApp;
import pl.estrix.model.SQLQuery;
import pl.estrix.persist.SQLLiteQueryBean;

public class MainOverviewController {
	
	static Logger LOG = Logger.getLogger(MainOverviewController.class);


	@FXML
	private TableView<SQLQuery> queryTable;
	@FXML
	private TableColumn<SQLQuery, String> nameQueryColumn;
	
	@FXML
	private Label infoLabel;

	@FXML
	private TextField nameField;
	@FXML
	private TextArea contentField;

	@FXML
	private Button newButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button executeButton;

	private Boolean isNew = false;
	
	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public MainOverviewController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		nameQueryColumn
				.setCellValueFactory(new PropertyValueFactory<SQLQuery, String>(
						"name"));

		queryTable.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<SQLQuery>() {
					public void changed(
							ObservableValue<? extends SQLQuery> observable,
							SQLQuery oldValue, SQLQuery newValue) {
						showQueryDetails(newValue);
					}
				});


		// Auto resize columns
		queryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void showQueryDetails(SQLQuery query) {
		if (query != null) {
			isNew = false;
			deleteButton.setDisable(false);
			nameField.setText(query.getName());
			contentField.setText(query.getContent());
		} else {
			isNew = true;
			deleteButton.setDisable(true);
			nameField.setText("");
			contentField.setText("");
		}
	}

	
	private void loadPerson(){		
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		queryTable.setItems(mainApp.getQueriesData());
	}

	public void disableButtons(Boolean disable){		
		if (disable){
			newButton.setDisable(true);
			saveButton.setDisable(true);
			deleteButton.setDisable(true);
			executeButton.setDisable(true);
		}else {
			newButton.setDisable(false);
			saveButton.setDisable(false);
			deleteButton.setDisable(false);
			executeButton.setDisable(false);
		}
	}

	private boolean isInputValid() {
		String errorMessage = "";

		if (nameField.getText() == null || nameField.getText().length() == 0) {
			errorMessage += "No valid first name!\n";
			nameField.setStyle("-fx-text-box-border: red ;");
			nameField.setTooltip(new Tooltip("Pone nie może byc puste."));
		}

		if (errorMessage.length() == 0) {
			nameField.setStyle("-fx-text-box-border: lightgrey  ;");
			nameField.setTooltip(null);
			return true;
		} else {
			// Show the error message
			return false;
		}
	}



	private void refreshQueryTable() {
		int selectedIndex = queryTable.getSelectionModel().getSelectedIndex();
		queryTable.setItems(null);
		queryTable.layout();
		queryTable.setItems(mainApp.getQueriesData());
		queryTable.getSelectionModel().select(selectedIndex);
	}


	public Label getInfoLabel() {
		return infoLabel;
	}

	@FXML
	private void handleSave() {
		LOG.debug("zapis");
		if (isInputValid()) {
			SQLQuery selected = null;
			if (isNew) {
				selected = new SQLQuery();
			} else {
				selected = queryTable.getSelectionModel().getSelectedItem();
			}

			selected.setName( (nameField.getText()!=null)?nameField.getText():"" );
			selected.setContent( (contentField.getText()!=null)?contentField.getText():"");

			SQLLiteQueryBean.save(selected);
			refreshQueryTable();
		}
	}

	@FXML
	private void handleNew() {
		showQueryDetails(null);
	}

	@FXML
	private void handleExecute() {

		infoLabel.setText("Wykontywanie zapytania.");
	}

	@FXML
	private void handleDelete() {
		int selectedIndex = queryTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex > 0) {
			SQLQuery selected = queryTable.getSelectionModel().getSelectedItem();
			SQLLiteQueryBean.delete(selected);
			queryTable.getItems().remove(selectedIndex);
			refreshQueryTable();
		} else {
			infoLabel.setText("Wybierz zapytanie do usunięcia.");
		}
	}

}
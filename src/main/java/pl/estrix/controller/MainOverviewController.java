package pl.estrix.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import pl.estrix.app.MainApp;
import pl.estrix.model.SQLQuery;
import pl.estrix.persist.SQLLiteQueryBean;
import pl.estrix.persist.SQLiteTargetBean;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainOverviewController {
	
	static Logger LOG = Logger.getLogger(MainOverviewController.class);

//

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
	TableView<List<String>> table = new TableView<>();

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
		Platform.runLater(() -> {
			table.getColumns().clear();
		});

		final CompletableFuture<List<Map<String, Object>>> completableFuture = CompletableFuture.supplyAsync(() -> SQLiteTargetBean.executeQuery((contentField.getText()!=null)?contentField.getText():""), mainApp.getExecutor());

		Platform.runLater(() -> completableFuture.whenComplete((res, ex) -> {
            if (ex != null) {
                LOG.warn(ex);
				infoLabel.setText("Problem z zapytaniem.");
            } else {
				table.getItems().clear();
				List<String> columns = new ArrayList<>();
				List<Map<String, Object>> records = res;
				for (int i = 0; i < records.size(); i++) {
					Map<String, Object> map = records.get(i);
					ObservableList<String> row = FXCollections.observableArrayList();
					for (Map.Entry<String, Object> entry : map.entrySet()) {
						if (i == 0) {
							columns.add(entry.getKey());
						}
						row.add(entry.getValue().toString());
					}
					Platform.runLater(() ->
							table.getItems().add(row));
				}
				for(int  k=0 ; k<columns.size(); k++) {
					final int j = k;
					TableColumn col = new TableColumn(columns.get(k));
					col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
						public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
							return new SimpleStringProperty(param.getValue().get(j).toString());
						}
					});
					Platform.runLater(() ->
							table.getColumns().addAll(col));
				}
            }
        }));
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
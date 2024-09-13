package com.sr.simplemanager;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportWindowController {

    private InventoryAppController inventoryAppController;
    @FXML
    private Text nameLoadFile= new Text("Cargar archivo ...");

    @FXML
    private Text totalLoadElements = new Text("0");


    //TABLES
    @FXML
    public TextField importPathField;

    @FXML
    private TableView<Item> importTable;

    @FXML
    private TableColumn<Item, Integer> importRowNumberCol;
    @FXML
    private TableColumn<Item, String> importSnImeiColumn;

    @FXML
    private TableColumn<Item, String> importRefColumn;

    @FXML
    private TableColumn<Item, String> importPurchaseDateColumn;

    @FXML
    private TableColumn<Item, Double> importCostPriceColumn;

    @FXML
    private TableColumn<Item, String> importSupplierColumn;

    @FXML
    private TableColumn<Item, Double> importSalePriceColumn;

    @FXML
    private TableColumn<Item, String> importClientColumn;
    @FXML
    private TableColumn<Item,String> importCompanyColumn;
    @FXML
    private TableColumn<Item, String> importDescriptionColumn;

    @FXML
    private Button importButton;

    @FXML
    public void initialize() {
        inventoryAppController = InventoryAppController.getInstance();
        mapCollumns();
        importButton.setDisable(true);

        importTable.getItems().addListener(new ListChangeListener<Object>() {
            @Override
            public void onChanged(Change<?> c) {
                while (c.next()) {
                    if (c.wasAdded() || c.wasRemoved()) {
                        if(!importTable.getItems().isEmpty()){
                            //enable import button
                            importButton.setDisable(false);
                        }
                        else {
                            //disable import button
                            importButton.setDisable(true);
                        }
                    }
                }
            }
        });
    }
    private void mapCollumns() {
        // Mapea las columnas a los datos de la clase Item
        importRowNumberCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        importSnImeiColumn.setCellValueFactory(new PropertyValueFactory<>("snImei"));
        importRefColumn.setCellValueFactory(new PropertyValueFactory<>("ref"));
        importDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        importPurchaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        importCostPriceColumn.setCellValueFactory(new PropertyValueFactory<>("precioCoste"));
        importSupplierColumn.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
        importSalePriceColumn.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        importClientColumn.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        importCompanyColumn.setCellValueFactory(new PropertyValueFactory<>("empresa"));
    }

    public void handlerImportChooseButton(ActionEvent event){
        //GET importPathField


        //GET fileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de datos");

        // Configurar el filtro de extensión
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        // Configurar el directorio inicial
        if (!this.importPathField.getText().isEmpty() && importPathField.getText() != null) {
            File initialDirectory = new File(importPathField.getText());
            fileChooser.setInitialDirectory(initialDirectory.getParentFile());
        }

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            importPathField.setText(file.getAbsolutePath()); // Mostrar la ruta en el campo de texto
        }
    }

    public void handleLoadButtonAction(ActionEvent event){

        //show alert if importPathField is empty
        if (importPathField.getText().isEmpty()) {
            inventoryAppController.showAlert("Error", "No se ha seleccionado ningún archivo", Alert.AlertType.WARNING);
            return;
        }
        //Clear table
        importTable.getItems().clear();

        //load data from file
        ExcelReader excelReader = new ExcelReader();

        //Read data from file
        try {
            //GET File Name
            nameLoadFile.setText(excelReader.getFileName(importPathField.getText()));
            //GET Items
            List<Item> items = excelReader.readExcelFile(importPathField.getText());
            //Add items to table
            importTable.getItems().addAll(items);
            //Update totalLoadElements
            totalLoadElements.setText(String.valueOf(importTable.getItems().size()));
        }catch (FileReaderException e) {
            inventoryAppController.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

    }
    public void insertItems(ActionEvent event){
        List<Item> items = new ArrayList<>(importTable.getItems());
        inventoryAppController.inserItems(items);
        inventoryAppController.showAlert("Información", "Los elementos han sido insertados correctamente", Alert.AlertType.INFORMATION);
        //CLOSE WINDOW
        ((Stage) importButton.getScene().getWindow()).close();
    }

}

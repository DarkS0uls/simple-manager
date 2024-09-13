package com.sr.simplemanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportWindowController {

    private InventoryAppController inventoryAppController;

    @FXML
    private TextField exportFolderPathField;

    @FXML
    private TextField exportFileNameField;

    @FXML
    private Button exportButton;


    @FXML
    public void initialize() {
        //get singleton instance of InventoryAppController
        inventoryAppController = InventoryAppController.getInstance();
        // Establecer el nombre de archivo predeterminado
        exportFileNameField.setText(getFileNameWithDate());
        exportButton.setDisable(true);
        // Establecer el manejador de eventos para el botón de exportación, solo habilitado cuando el folder y el nombre estan completos
        exportFolderPathField.textProperty().addListener((observable, oldValue, newValue) -> {
            exportButton.setDisable(exportFolderPathField.getText().isEmpty() || newValue.isEmpty());
        });
        exportFileNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            exportButton.setDisable(exportFolderPathField.getText().isEmpty() || newValue.isEmpty());
        });


    }

    private Boolean validateFileExtension(String fileName){
        return fileName.endsWith(".xlsx");
    }
    private String getFileNameWithDate() {
        // Obtener la fecha actual
        LocalDate currentDate = LocalDate.now();

        // Crear un formateador de fecha
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");

        // Formatear la fecha actual
        String formattedDate = currentDate.format(dateFormatter);

        // Crear el nombre del archivo
        String fileName = "inventario_" + formattedDate+".xlsx";
        return fileName;
    }

    public void handleSelectFolderButtonAction(ActionEvent event){
        // Crear un DirectoryChooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de destino");

        // Mostrar el diálogo de selección de directorio y obtener la carpeta seleccionada
        File selectedFolder = directoryChooser.showDialog(null);

        // Comprobar si se seleccionó una carpeta
        if (selectedFolder != null) {
            // Obtener la ruta de la carpeta seleccionada y establecerla en el campo de texto
            exportFolderPathField.setText(selectedFolder.getAbsolutePath());
        }
    }
    public void handleExportButtonAction(ActionEvent event){
        if(validateFileExtension(exportFileNameField.getText())){
            // Exportar los datos a un archivo Excel
            //1* Obtengo datos para exportar
            List<Item> items=inventoryAppController.getExportData();
            //2* Creo un objeto de la clase ExcelExporter
            ExcelExporter excelExporter = new ExcelExporter();
            //3* Exporto los datos
            try{
                excelExporter.export(items, exportFolderPathField.getText(), exportFileNameField.getText());
            } catch (Exception e) {
                //e.printStackTrace();
                inventoryAppController.showAlert("Error de exportación", "Error al exportar los datos.", Alert.AlertType.ERROR);
            }


            // Mostrar un mensaje de confirmación
            inventoryAppController.showAlert("Exportación completada", "Los datos se han exportado correctamente.", Alert.AlertType.CONFIRMATION);
        }
        else{
            // Mostrar un mensaje de error si la extensión del archivo no es válida
            inventoryAppController.showAlert("Error de extensión", "El nombre del archivo debe terminar con '.xlsx'.", Alert.AlertType.WARNING);
        }
    }
}

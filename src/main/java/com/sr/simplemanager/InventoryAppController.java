package com.sr.simplemanager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.UnaryOperator;

public class InventoryAppController {


    //SINGLETON INSTANCE
    private static InventoryAppController instance;
    //END SINGLETON
    private static final String CONFIG_FILE = "config.properties";
    private String pathFieldData;

    private DatabaseManager databaseManager;



    //FXML DATA

    @FXML
    private ImageView logoImageView;
    //FXML DATA FILTERS

    private String lastQuery;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField snImeiField;

    @FXML
    private TextField empresaField;

    @FXML
    private TextField refField;

    @FXML
    private TextField proveedorField;

    @FXML
    private TextField clienteField;

    @FXML
    private ComboBox<String> orderByComboBox;

    @FXML
    private ComboBox<String> orderTypeComboBox;

    //END FILTERS
    //FXML DATA TABLE

    @FXML
    private TableView<Item> dataTable;

    @FXML
    private TableColumn<Item, Integer> rowNumberCol;
    @FXML
    private TableColumn<Item, String> snImeiColumn;

    @FXML
    private TableColumn<Item, String> refColumn;

    @FXML
    private TableColumn<Item, String> descriptionColumn;

    @FXML
    private TableColumn<Item, String> purchaseDateColumn;

    @FXML
    private TableColumn<Item, Double> costPriceColumn;

    @FXML
    private TableColumn<Item, String> supplierColumn;

    @FXML
    private TableColumn<Item, Double> salePriceColumn;

    @FXML
    private TableColumn<Item, String> clientColumn;

    @FXML TableColumn<Item,String> company;
    //END TABLE
    //PAGINATION
    @FXML
    private TextField pageInputField;

    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    @FXML
    private Text totalElementsText;
    @FXML
    private Text totalPagesText;

    @FXML
    private Text currentPageText;

    private int totalElements;

    private int totalPages;

    private int currentPage;


    //END PAGINATION


    @FXML
    public void initialize() {
        //SINGLETON INSTANCE
        instance = this;
        //SET MAP COLUMNS
        mapCollumns();
        //SET VALIDATIONS
        setDatePickerValidations();
        //GET PATH FROM PROPERTIES
        getDbPath();
        //DEFINE DEFAULT PAGE SIZE AND PAGE NUMBER
        defineDefaultPageSizeAndPageNumber();
        //DEFINE FORMAT PAGE INPUT FIELD
        formatPageInputField();
        //GET CONNECTION
        initDatabaseConnection();
        //GET TOTAL ELEMENTS
        getTotalElements();
        //GET TOTAL PAGES
        getTotalPages();
        //pageNumberEventListener
        pageNumberEventListener();
        //pageSizeEventListener
        pageSizeEventListener();
        //last query default value
        this.lastQuery = "";
        //orderBy default values
        orderByComboBox.setValue("FECHA COMPRA");
        orderTypeComboBox.setValue("DESC");




        /* Lógica para cargar datos en la tabla, puedes poblarla con tus datos aquí
        ObservableList<Item> data = FXCollections.observableArrayList(
                new Item("123456789012345", "A1", "Producto A", LocalDate.of(2023, 1, 10).toString(), 100.0, "Proveedor A", 150.0, "Cliente A"),
                new Item("987654321098765", "B2", "Producto B", LocalDate.of(2023, 2, 15).toString(), 200.0, "Proveedor B", 250.0, "Cliente B"),
                new Item("112233445566778", "C3", "Producto C", LocalDate.of(2023, 3, 20).toString(), 300.0, "Proveedor C", 350.0, "Cliente C")
        );
        // Asignar los datos al TableView
        dataTable.setItems(data);
        */
    }
    public static InventoryAppController getInstance() {
        return instance;
    }

    private void mapCollumns() {
        // Mapea las columnas a los datos de la clase Item
        rowNumberCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        snImeiColumn.setCellValueFactory(new PropertyValueFactory<>("snImei"));
        refColumn.setCellValueFactory(new PropertyValueFactory<>("ref"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        purchaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        costPriceColumn.setCellValueFactory(new PropertyValueFactory<>("precioCoste"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
        salePriceColumn.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        company.setCellValueFactory(new PropertyValueFactory<>("empresa"));

    }

    private void getDbPath() {
        // Leer la ruta de la base de datos desde el archivo de propiedades
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            properties.load(in);
            String dbPath = properties.getProperty("db.path");
            if (dbPath != null && !dbPath.isEmpty()) {
                // Puedes usar esta ruta para inicializar el campo de texto o la conexión a la base de datos
                System.out.println("Ruta de la base de datos cargada: " + dbPath);
                // Por ejemplo, puedes configurar el campo de texto
                //pathField.setText(dbPath);
                this.pathFieldData = dbPath;
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de propiedades: " + e.getMessage());
            this.pathFieldData = "";
        }
    }

    private void defineDefaultPageSizeAndPageNumber() {
        // Define el tamaño de la página predeterminado y el número de página predeterminado
        pageSizeComboBox.setValue(500);
        setCurrentPage(0);
    }

    private void setCurrentPage(int page) {
        this.currentPage = page;
        currentPageText.setText(String.valueOf(page + 1));
    }

    private void formatPageInputField() {
        // Agregar un TextFormatter para permitir solo números enteros
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // \\d* matches all digits
                return change;
            }
            return null;
        };
        TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), 1, integerFilter);
        pageInputField.setTextFormatter(textFormatter);
    }

    private void initDatabaseConnection() {
        System.out.println("validate connexion a la base de datos, PathFieldData: " + this.pathFieldData);
        if (!this.pathFieldData.isEmpty() && this.pathFieldData != null) {
            //Codigo para mostrar alerta si la base de datos no se puede conectar
            databaseManager = new DatabaseManager(this.pathFieldData);
            if (databaseManager.connect() == null) {
                showAlert("Error de conexión", "No se pudo conectar a la base de datos.", Alert.AlertType.ERROR);
            } else {
                if (databaseManager.checkTableExists()) {
                    showAlert("Conexión exitosa", "Conexión exitosa a la base de datos.", Alert.AlertType.CONFIRMATION);
                    // Obtener los datos de la base de datos y poblar la tabla
                    loadPage(this.currentPage, pageSizeComboBox.getValue());
                    /*
                    List<Item> items = databaseManager.fetchDataFromDatabase(currentPage, PAGE_SIZE);
                    ObservableList<Item> data = FXCollections.observableArrayList(items);
                    dataTable.setItems(data);
                    */
                } else {
                    showAlert("Error de conexión", "No se pudo conectar a la base de datos.", Alert.AlertType.ERROR);
                }
            }
        }
    }


    private void getTotalElements() {
        this.totalElements = databaseManager.getTotalItems();
        totalElementsText.setText(String.valueOf(this.totalElements));
    }

    private void getTotalPages() {
        this.totalPages = (int) Math.ceil((double) this.totalElements / pageSizeComboBox.getValue());
        totalPagesText.setText(String.valueOf(this.totalPages));
    }

    private void pageNumberEventListener() {
        //Creamos un listener para el cambio de tamaño de pagina
        // Agregar un ChangeListener a pageInputField
        pageInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    int newPage = Integer.parseInt(newValue);
                    if (newPage < 1 || newPage > this.totalPages) {
                        // Mostrar un mensaje de error si el valor ingresado es inválido
                        showAlert("Error de entrada", "Por favor, ingrese un número de página válido. Pagina:[1..." + totalPages + "]", Alert.AlertType.ERROR);
                        // Restablecer pageInputField al valor anterior
                        pageInputField.setText(oldValue);
                    } else {
                        System.out.println("Cambio de pagina a: " + newPage);
                        //Configuro la pagina actual
                        this.currentPage = newPage - 1;
                        //Identifico si la consulta es con query o sin query
                        //1* Obtengo el query actual
                        String query = getQuery();
                        if(lastQuery.equals(query) && !lastQuery.isEmpty()){
                            //Estamos realizando una consulta, con query
                            searchWithPagination(this.currentPage, pageSizeComboBox.getValue());
                        }
                        else{
                            //disparo mi loadPage()
                            loadPage(this.currentPage, pageSizeComboBox.getValue());

                        }



                    }
                } catch (NumberFormatException e) {
                    // Mostrar un mensaje de error si el texto ingresado no es un número
                    showAlert("Error de entrada", "Por favor, ingrese un número de página válido.", Alert.AlertType.ERROR);
                    // Restablecer pageInputField al valor anterior
                    pageInputField.setText(oldValue);
                }
            }
        });
    }

    private void pageSizeEventListener() {
        // Agregar un ChangeListener a pageSizeComboBox
        pageSizeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("El tamaño de la página ha cambiado a: " + newValue);
                //nos ubicamos en la pagina 0
                setCurrentPage(0);
                this.pageInputField.setText("1");
                //Identifico si la consulta es con query o sin query
                //1* Obtengo el query actual
                String query = getQuery();
                if(lastQuery.equals(query) && !lastQuery.isEmpty()){
                    //Estamos realizando una consulta, con query
                    searchWithPagination(this.currentPage, pageSizeComboBox.getValue());
                }
                else{
                    //disparo mi loadPage()
                    loadPage(this.currentPage, pageSizeComboBox.getValue());
                    // Recalcular el total de páginas

                }
                getTotalPages();
            }
        });
    }


    public void loadPage(int pageNumber, int pageSize) {
        //Obtengo la pagina y tamaño de pagina actual
        List<Item> items = databaseManager.fetchDataFromDatabase(pageNumber * pageSize, pageSize);
        ObservableList<Item> data = FXCollections.observableArrayList(items);
        dataTable.setItems(data);
    }

    @FXML
    private void search() {
        System.out.println("Buscando...");
        //obtengo informacion de paginacion
        int pageSize = pageSizeComboBox.getValue();

        //Obtengo la query
        String query = getQuery();
        //Si la consulta no cambio, no hago nada, retorno.
        if (lastQuery.equals(query)) {
            return;
        }
        //la consulta cambio, actualizo la pagina actual a 0 y actualizo mi ultima consulta.
        //1* Guardo la consulta
        this.lastQuery = query;
        //2* Actualizo la pagina actual
        setCurrentPage(0);
        this.pageInputField.setText("1");

        //3*Obtiene todos los datos que se ajustan a la busqueda
        List<Item> items = databaseManager.getItemsByQuery(query.toString());
        List<Item> pageItems = new ArrayList<>();
        //4* actualizo el total de elementos
        this.totalElements = items.size();
        totalElementsText.setText(String.valueOf(this.totalElements));
        //5* actualizo el total de paginas
        getTotalPages();
        //6* retorno solo la pagina actual, con el tamaño indicado por el cliente.
        pageItems = items.subList(this.currentPage * pageSize, Math.min((this.currentPage + 1) * pageSize, items.size()));
        //7* actualizo la tabla
        ObservableList<Item> data = FXCollections.observableArrayList(pageItems);
        dataTable.setItems(data);
    }

    public List<Item> getExportData() {
        //Obtengo la query
        String query = getQuery();
        //3*Obtiene todos los datos que se ajustan a la busqueda
        return databaseManager.getItemsByQuery(query);

    }





    private void searchWithPagination(int pageNumber, int pageSize){
        //Obtengo la query
        String query = getQuery();
        //1*Obtiene todos los datos que se ajustan a la busqueda
        List<Item> items = databaseManager.getItemsByQuery(query.toString());
        List<Item> pageItems = new ArrayList<>();

        //2* retorno solo la pagina actual, con el tamaño indicado por el cliente.
        pageItems = items.subList(pageNumber * pageSize, Math.min((pageNumber + 1) * pageSize, items.size()));
        //7* actualizo la tabla
        ObservableList<Item> data = FXCollections.observableArrayList(pageItems);
        dataTable.setItems(data);

    }

    private String getDatePickerTextValue(DatePicker datePicker) {
        TextField datePickerTextField = (TextField) datePicker.lookup(".text-field");
        return datePickerTextField.getText();
    }

    private String getQuery(){
        //Obtener los valores de los campos de filtro
        LocalDate startDate = startDatePicker.getValue();
        String startDateText = getDatePickerTextValue(startDatePicker);

        LocalDate endDate = endDatePicker.getValue();
        String endDateText = getDatePickerTextValue(endDatePicker);
        String snImei = snImeiField.getText();
        String empresa= empresaField.getText();
        String ref = refField.getText();
        String proveedor = proveedorField.getText();
        String cliente = clienteField.getText();
        System.out.println("SEARCH ==>startDate: " + startDate + " endDate: " + endDate + " snImei: " + snImei + " ref: " + ref + " proveedor: " + proveedor + " cliente: " + cliente);

        StringBuilder query = new StringBuilder("SELECT * FROM inventario WHERE 1=1");

        if (startDate != null && !startDateText.isEmpty()) {
            query.append(" AND fechaCompra >= '").append(startDate.toString()).append("'");
        }

        if (endDate != null && !endDateText.isEmpty()) {
            query.append(" AND fechaCompra <= '").append(endDate.toString()).append("'");
        }

        if (!snImei.isEmpty()) {
            query.append(" AND snImei LIKE '%").append(snImei).append("%'");
        }
        if(!empresa.isEmpty()){
            query.append(" AND empresa LIKE '%").append(empresa).append("%'");
        }

        if (!ref.isEmpty()) {
            query.append(" AND ref LIKE '%").append(ref).append("%'");
        }

        if (!proveedor.isEmpty()) {
            query.append(" AND proveedor LIKE '%").append(proveedor).append("%'");
        }

        if (!cliente.isEmpty()) {
            query.append(" AND cliente LIKE '%").append(cliente).append("%'");
        }
        //ORDER BY
        if(!orderByComboBox.getValue().isEmpty() && !orderByComboBox.getValue().equals("NULL") ){
            switch (orderByComboBox.getValue()){
                case "FECHA COMPRA":
                    query.append(" ORDER BY ").append("fechaCompra");
                    break;
                case "PRECIO COSTE":
                    query.append(" ORDER BY ").append("precioCoste");
                    break;
                case "PRECIO VENTA":
                    query.append(" ORDER BY ").append("precioVenta");
                    break;
                default:
                    break;

            }
            if (!orderTypeComboBox.getValue().isEmpty()) {
                query.append(" ").append(orderTypeComboBox.getValue());
            }
        }


        System.out.println("SEARCH ==>query: " + query.toString());
        return query.toString();

    }

    private void setDatePickerValidations() {
        // Añadir ChangeListener a startDatePicker
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.toString().isEmpty() ) {
                System.out.println("DatePicker ha sido limpiado");
                // Aquí puedes actualizar tu consulta SQL o realizar cualquier otra acción necesaria
            }
            System.out.println("startDatePicker changed: " + newValue);
            validateDates();
        });

        // Añadir ChangeListener a endDatePicker
        endDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                if (newValue == null) {
                    System.out.println("DatePicker ha sido limpiado");
                    // Aquí puedes actualizar tu consulta SQL o realizar cualquier otra acción necesaria
                }
                System.out.println("endDatePicker changed: " + newValue);
                validateDates();
            }
        });
    }

    private void validateDates() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        LocalDate currentDate = LocalDate.now(); // Obtener la fecha actual

        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                // Mostrar un mensaje de error y restablecer endDatePicker
                System.out.println("La fecha final debe ser superior o igual a la inicial.");
                showAlert("Error de fecha", "La fecha final debe ser superior o igual a la inicial.", Alert.AlertType.ERROR);
                endDatePicker.setValue(null);
            }
        }

        if (startDate != null) {
            if (startDate.isAfter(currentDate)) {
                // Mostrar un mensaje de error y restablecer startDatePicker
                System.out.println("La fecha inicial no puede ser mayor a la fecha actual.");
                showAlert("Error de fecha", "La fecha inicial no puede ser mayor a la fecha actual.", Alert.AlertType.ERROR);
                startDatePicker.setValue(null);
            }
        }

        if (endDate != null) {
            if (endDate.isAfter(currentDate)) {
                // Mostrar un mensaje de error y restablecer endDatePicker
                System.out.println("La fecha final no puede ser mayor a la fecha actual.");
                showAlert("Error de fecha", "La fecha final no puede ser mayor a la fecha actual.", Alert.AlertType.ERROR);
                endDatePicker.setValue(null);
            }
        }
        System.out.println("startDate: " + startDate + " AND endDate: " + endDate);
    }


    public void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
        // Traer la alerta al frente
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.toFront();
        // Hacer que la alerta siempre esté en primer plano
        alertStage.setAlwaysOnTop(true);
    }


    // Método para abrir la base de datos SQLite
    // Método para abrir la ventana emergente
    // Método para abrir la ventana emergente
    public void openDatabaseDialog(ActionEvent event) {
        // Obtener el escenario primario (si es necesario)
        Stage primaryStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        // Crear una nueva ventana (Stage)
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.setTitle("Seleccionar Base de Datos");

        // Contenedor vertical
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 10;");

        // Etiqueta
        Label label = new Label("Ingrese la ruta o seleccione el archivo de la base de datos:");

        // Campo de texto para ingresar la ruta manualmente
        TextField pathField = new TextField();
        pathField.setText(this.pathFieldData);
        pathField.setPromptText("Ruta de la base de datos");

        // Botón para abrir el FileChooser
        Button chooseFileButton = new Button("Seleccionar archivo...");
        chooseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo SQLite");

            // Configurar el filtro de extensión
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQLite files (*.db)", "*.db");
            fileChooser.getExtensionFilters().add(extFilter);

            // Configurar el directorio inicial
            if (!pathField.getText().isEmpty() && pathField.getText() != null) {
                File initialDirectory = new File(pathField.getText());
                fileChooser.setInitialDirectory(initialDirectory.getParentFile());
            }

            File file = fileChooser.showOpenDialog(dialogStage);
            if (file != null) {
                pathField.setText(file.getAbsolutePath()); // Mostrar la ruta en el campo de texto
            }
        });

        // Botón para descargar el archivo de ejemplo de la base de datos
        Button downloadSampleDbButton = new Button("Descargar archivo de ejemplo...");
        downloadSampleDbButton.setOnAction(e -> {
            try {
                // Ruta del archivo de ejemplo en la carpeta de recursos
                String sampleDbPath = "example_inventory.db";

                // Crear un InputStream para leer el archivo de ejemplo
                InputStream is = getClass().getClassLoader().getResourceAsStream(sampleDbPath);
                if (is == null) {
                    System.out.println("Archivo no encontrado en resources.");
                    return;
                } else {
                    // Usar el InputStream como necesites
                    System.out.println("Archivo cargado exitosamente.");
                }

                // Crear un DirectoryChooser
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Seleccionar carpeta de salida");
                File selectedDirectory = directoryChooser.showDialog(dialogStage);

                if (selectedDirectory == null) {
                    System.out.println("No se seleccionó ninguna carpeta.");
                    return;
                }

                // Crear un OutputStream para escribir el archivo de ejemplo en la ubicación deseada
                String outputPath = selectedDirectory.getAbsolutePath() + File.separator + "example.db";
                File outputFile = new File(outputPath);
                OutputStream os = new FileOutputStream(outputFile);

                // Copiar el archivo de ejemplo
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                // Cerrar los streams
                is.close();
                os.close();

                // Mostrar un mensaje de éxito
                showAlert("Archivo descargado", "El archivo de ejemplo de la base de datos se ha descargado correctamente.", Alert.AlertType.INFORMATION);
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error al descargar el archivo", "No se pudo descargar el archivo de ejemplo de la base de datos.", Alert.AlertType.ERROR);
            }
        });



        // Botón para confirmar la selección
        Button confirmButton = new Button("Aceptar");
        confirmButton.setOnAction(e -> {
            String dbPath = pathField.getText();
            if (!dbPath.isEmpty()) {
                // Guardar la ruta en el archivo de propiedades
                Properties properties = new Properties();
                properties.setProperty("db.path", dbPath);
                try (FileOutputStream out = new FileOutputStream("config.properties")) {
                    properties.store(out, null);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                // Aquí puedes usar el valor de dbPath para establecer la conexión a la base de datos
                System.out.println("Ruta de la base de datos seleccionada: " + dbPath);
                //valida conexion a la base de datos
                this.pathFieldData = dbPath;
                initDatabaseConnection();
                // Cierra el diálogo
                dialogStage.close();
            }
        });

        // Disposición horizontal para el botón de selector y el campo de texto
        HBox hbox = new HBox(10, pathField, chooseFileButton);
        HBox.setHgrow(pathField, Priority.ALWAYS);

        // Añadir componentes al VBox
        vbox.getChildren().addAll(label, hbox, confirmButton, downloadSampleDbButton);
        // Añadir componentes al VBox

        // Crear la escena y mostrar la ventana
        Scene dialogScene = new Scene(vbox, 400, 150);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
        // Establecer el foco en el campo de texto
        pathField.requestFocus();
    }

    public void handleImportButtonAction(ActionEvent event) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImportWindow.fxml"));
            Parent root = fxmlLoader.load();

            // Crear una nueva ventana (Stage) y mostrarla
            Stage stage = new Stage();
            stage.setTitle("Importar archivo");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handlerExportButtonAction(ActionEvent event){
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ExportWindow.fxml"));
            Parent root = fxmlLoader.load();

            // Crear una nueva ventana (Stage) y mostrarla
            Stage stage = new Stage();
            stage.setTitle("Exportar Archivo");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inserItems(List<Item> itemsToAdd) {
        // Insertar los elementos en la base de datos

        databaseManager.insertItems(itemsToAdd);

        // Actualizar la tabla
        loadPage(this.currentPage, pageSizeComboBox.getValue());
        //recalcular total de elementos
        getTotalElements();
        //recalcular total de paginas
        getTotalPages();
    }

}
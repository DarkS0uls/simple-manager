package com.sr.simplemanager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class DatabaseManager {

    private String dbPath;

    private static final String TABLE_NAME = "inventario";

    public DatabaseManager(String dbPath) {
        this.dbPath = dbPath;
    }

    public Connection connect() {
        try {
            // Establecer la conexión a la base de datos SQLite
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error de conexión", "No se pudo conectar a la base de datos.");
            return null;
        }
    }

    public boolean checkTableExists() {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'";
        try (Connection connection = connect(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            return rs.next(); // Si rs.next() devuelve true, la tabla existe
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error de consulta", "Error al verificar la existencia de la tabla.");
            return false;
        }
    }
    public List<Item> insertItems(List<Item> items) {
        String query = "INSERT INTO " + TABLE_NAME + " (snImei, ref, descripcion, fechaCompra, precioCoste, proveedor, precioVenta, cliente,empresa) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        try (Connection connection = connect(); PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (Item item : items) {
                pstmt.setString(1, item.getSnImei());
                pstmt.setString(2, item.getRef());
                pstmt.setString(3, item.getDescripcion());
                pstmt.setString(4, item.getFechaCompra());
                pstmt.setDouble(5, item.getPrecioCoste());
                pstmt.setString(6, item.getProveedor());
                pstmt.setDouble(7, item.getPrecioVenta());
                pstmt.setString(8, item.getCliente());
                pstmt.setString(9, item.getEmpresa());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error de inserción", "Error al insertar los datos en la tabla.");
        }
        return items;
    }
    public List<Item> fetchDataFromDatabase(int offset, int limit) {
        String query = "SELECT * FROM " + TABLE_NAME+" LIMIT " + limit + " OFFSET " + offset;
        System.out.println("LAZY QUERY:"+query);
        List<Item> items = new ArrayList<>();


        try (
                Connection connection = connect();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            int elementNumber=1;
            while (rs.next()) {
                int id = elementNumber;
                String snImei = rs.getString("snImei");
                String ref = rs.getString("ref");
                String descripcion = rs.getString("descripcion");
                String fechaCompra = rs.getString("fechaCompra");
                double precioCoste = rs.getDouble("precioCoste");
                String proveedor = rs.getString("proveedor");
                double precioVenta = rs.getDouble("precioVenta");
                String cliente = rs.getString("cliente");
                String empresa = rs.getString("empresa");

                Item item = new Item(id,snImei, ref, descripcion, fechaCompra, precioCoste, proveedor, precioVenta, cliente,empresa);
                items.add(item);
                elementNumber++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error de consulta", "Error al obtener los datos de la tabla.");
        }

        return items;
    }



    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public int getTotalItems() {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        try (Connection connection = connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.getInt(1); // Devuelve el número total de filas
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error de consulta", "Error al obtener el total de elementos de la tabla.");
            return -1;
        }
    }

    public List<Item> getItemsByQuery(String sql) {
        List<Item> items = new ArrayList<>();

        try (Connection connection = connect(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            int elementNumber=1;
            while (rs.next()) {
                int id = elementNumber;
                String snImei = rs.getString("snImei");
                String ref = rs.getString("ref");
                String descripcion = rs.getString("descripcion");
                String fechaCompra = rs.getString("fechaCompra");
                double precioCoste = rs.getDouble("precioCoste");
                String proveedor = rs.getString("proveedor");
                double precioVenta = rs.getDouble("precioVenta");
                String cliente = rs.getString("cliente");
                String empresa = rs.getString("empresa");

                Item item = new Item(id,snImei, ref, descripcion, fechaCompra, precioCoste, proveedor, precioVenta, cliente,empresa);
                items.add(item);
                elementNumber++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error de consulta", "Error al obtener los datos de la tabla.");
        }

        return items;
    }
}

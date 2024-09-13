package com.sr.simplemanager;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public ExcelExporter() {
    }

    public void export(List<Item> items, String folderPath,String fileName) throws IOException {
        // Crear un nuevo libro de trabajo
        Workbook workbook = new XSSFWorkbook();

        // Crear una nueva hoja
        Sheet sheet = workbook.createSheet("inventario");

        // Crear la fila de encabezado
        Row headerRow = sheet.createRow(0);


        Cell snImei = headerRow.createCell(0);
        snImei.setCellValue("SN_IMEI");

        Cell ref = headerRow.createCell(1);
        ref.setCellValue("REF");

        Cell descripcion = headerRow.createCell(2);
        descripcion.setCellValue("DESCRIPCION");

        Cell fechaCompra = headerRow.createCell(3);
        fechaCompra.setCellValue("FECHA_COMPRA");

        Cell precioCoste = headerRow.createCell(4);
        precioCoste.setCellValue("PRECIO_COSTE");

        Cell proveedor = headerRow.createCell(5);
        proveedor.setCellValue("PROVEEDOR");

        Cell precioVenta = headerRow.createCell(6);
        precioVenta.setCellValue("PRECIO_VENTA");

        Cell cliente = headerRow.createCell(7);
        cliente.setCellValue("CLIENTE");

        Cell empresa = headerRow.createCell(8);
        empresa.setCellValue("EMPRESA");

        // Agregar los datos
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            // Crear una nueva fila
            Row row = sheet.createRow(i + 1);

            // Crear las celdas de la fila
            Cell _snImei = row.createCell(0);
            _snImei.setCellValue(item.getSnImei());

            Cell _ref = row.createCell(1);
            _ref.setCellValue(item.getRef());

            Cell _descripcion = row.createCell(2);
            _descripcion.setCellValue(item.getDescripcion());

            Cell _fechaCompra = row.createCell(3);
            _fechaCompra.setCellValue(item.getFechaCompra());

            Cell _precioCoste = row.createCell(4);
            _precioCoste.setCellValue(item.getPrecioCoste());

            Cell _proveedor = row.createCell(5);
            _proveedor.setCellValue(item.getProveedor());

            Cell _precioVenta = row.createCell(6);
            _precioVenta.setCellValue(item.getPrecioVenta());

            Cell _cliente = row.createCell(7);
            _cliente.setCellValue(item.getCliente());

            Cell _empresa = row.createCell(8);
            _empresa.setCellValue(item.getEmpresa());

        }

        // Escribir el libro de trabajo en un archivo
        try (FileOutputStream fileOut = new FileOutputStream(folderPath+ "/" + fileName)) {
            workbook.write(fileOut);
        }

        // Cerrar el libro de trabajo
        workbook.close();
    }
}

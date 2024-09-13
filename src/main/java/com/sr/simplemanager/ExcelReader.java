package com.sr.simplemanager;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelReader {


    // Define the expected column names
    private static final String[] expectedColumns = {"SN_IMEI", "REF", "DESCRIPCION", "FECHA_COMPRA", "PRECIO_COSTE", "PROVEEDOR", "PRECIO_VENTA", "CLIENTE", "EMPRESA"};

    public ExcelReader() {
    }
    public String getFileName(String filePath) {
        String[] parts = filePath.split("/");
        return parts[parts.length - 1];
    }

    public List<Item> readExcelFile(String filePath) throws FileReaderException {
        List<Item> itemList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);


            Iterator<Row> rows = sheet.iterator();
            Map<String, Integer> columnIndices = getColumnIndices(rows.next());
            String[] missingColumns = validateColumns(columnIndices);
            if (missingColumns.length > 0) {
                // Missing columns
                System.out.println("Missing columns: " + Arrays.toString(missingColumns));
                throw new FileReaderException("Missing columns: " + Arrays.toString(missingColumns));
            }
            System.out.println("All columns are present");
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (currentRow.getRowNum() == 0) {
                    // Skip header row
                    continue;
                }


                Cell snImeiCell = currentRow.getCell(columnIndices.get("SN_IMEI"));
                if(snImeiCell==null) break;

                snImeiCell.setCellType(CellType.STRING);
                String snImei = snImeiCell.getStringCellValue();
                System.out.println("snImei: " + snImei);

                if (snImei.isEmpty()) break;

                String ref = "";
                try {
                    Cell refCell = currentRow.getCell(columnIndices.get("REF"));
                    refCell.setCellType(CellType.STRING);
                    ref = refCell.getStringCellValue();
                    //System.out.println("ref: " + ref);
                } catch (Exception e) {
                    throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna REF");
                }
                String description = "";
                try {
                    Cell descripcionCell = currentRow.getCell(columnIndices.get("DESCRIPCION"));
                    descripcionCell.setCellType(CellType.STRING);
                    description = descripcionCell.getStringCellValue();
                } catch (Exception e) {
                    throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna DESCRIPCION");
                }


                String fechaCompra = "";
                try {
                    Cell fechaCompraCell = currentRow.getCell(columnIndices.get("FECHA_COMPRA"));
                    //GET Cell Type
                    CellType fechaCompraCellType=fechaCompraCell.getCellType();
                    if(fechaCompraCellType==CellType.NUMERIC){

                        if (DateUtil.isCellDateFormatted(fechaCompraCell)) {
                            // La celda es de tipo fecha
                            Date dateFechaCompra = fechaCompraCell.getDateCellValue();
                            // Ahora puedes convertir la fecha a String o a cualquier formato que necesites
                            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
                            fechaCompra = isoFormat.format(dateFechaCompra);
                        } else {
                            // La celda no es de tipo fecha
                            throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna FECHA_COMPRA, formato incorrecto, valide que el formato sea de fecha(dd/mm/yyyy)");
                        }
                    }
                    else if(fechaCompraCellType==CellType.STRING){
                            //validate date format
                            fechaCompra = fechaCompraCell.getStringCellValue();
                            if(validateDateFormat(fechaCompra)){
                                throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna FECHA_COMPRA, formato incorrecto");
                            }
                            //convert to ISO Format
                            fechaCompra = convertDateFormat(fechaCompra);
                    }
                    else {
                            throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna FECHA_COMPRA, formato incorrecto, valide que el formato sea de fecha(dd/mm/yyyy)");
                    }

                } catch (Exception e) {
                    System.out.println("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna FECHA_COMPRA, detalle: " + e.getMessage());
                    throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna FECHA_COMPRA");
                }

                double precioCoste = 0;
                try {
                    Cell precioCosteCell = currentRow.getCell(columnIndices.get("PRECIO_COSTE"));
                    precioCosteCell.setCellType(CellType.NUMERIC);
                    precioCoste = precioCosteCell.getNumericCellValue();
                }catch (Exception e) {
                    throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna PRECIO_COSTE");
                }

                String proveedor = "";
                try {
                    Cell proveedorCell = currentRow.getCell(columnIndices.get("PROVEEDOR"));
                    proveedorCell.setCellType(CellType.STRING);
                    proveedor = proveedorCell.getStringCellValue();
                }catch (Exception e) {
                    throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna PROVEEDOR");
                }


                double precioVenta = 0;
                try {
                    Cell precioVentaCell = currentRow.getCell(columnIndices.get("PRECIO_VENTA"));
                    precioVentaCell.setCellType(CellType.NUMERIC);
                    precioVenta = precioVentaCell.getNumericCellValue();
                }catch (Exception e) {
                    throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna PRECIO_VENTA");
                }

                String cliente = "";
                try {

                    Cell clienteCell = currentRow.getCell(columnIndices.get("CLIENTE"));
                    clienteCell.setCellType(CellType.STRING);
                    cliente = clienteCell.getStringCellValue();
                }catch (Exception e) {
                    throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna CLIENTE");
                }

                String empresa = "";
                try {
                    Cell empresaCell = currentRow.getCell(columnIndices.get("EMPRESA"));
                    empresaCell.setCellType(CellType.STRING);
                    empresa = empresaCell.getStringCellValue();
                }
                catch (Exception e) {
                    throw new FileReaderException("Error en la fila: " + currentRow.getRowNum() + ", con SN_IMEI:" + snImei + ", en la columna EMPRESA");
                }



                //ADD ITEMS
                Item item = new Item();
                // item.setId((int) snImeiCell.getNumericCellValue());
                item.setSnImei(snImei);
                item.setRef(ref);
                item.setDescripcion(description);
                item.setFechaCompra(fechaCompra);
                item.setPrecioCoste(precioCoste);
                item.setProveedor(proveedor);
                item.setPrecioVenta(precioVenta);
                item.setCliente(cliente);
                item.setEmpresa(empresa);
                //ADD ITEM TO LIST
                itemList.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return itemList;
    }



    private Map<String, Integer> getColumnIndices(Row headerRow) {
        Map<String, Integer> columnIndices = new HashMap<>();
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            cell.setCellType(CellType.STRING);
            columnIndices.put(cell.getStringCellValue(), cell.getColumnIndex());
            System.out.println(cell.getStringCellValue() + " " + cell.getColumnIndex());
        }
        return columnIndices;
    }

    private String[] validateColumns(Map<String, Integer> columnIndices) {
        List<String> missingColumns = new ArrayList<>();
        for (String expectedColumn : expectedColumns) {
            if (!columnIndices.containsKey(expectedColumn)) {
                // Column is missing
                missingColumns.add(expectedColumn);
            }
        }
        return missingColumns.toArray(new String[0]);
    }

    private boolean validateDateFormat(String date) {
        return date.matches("\\d{2}\\/\\d{2}\\/\\d{4}");
    }
    private String convertDateFormat(String date) {
        System.out.println("Convert Date: " + date);
        String[] parts = date.split("/");
        for(int i=0;i<parts.length;i++){
            System.out.println(parts[i]);
        }
        return parts[2] + "-" + parts[1] + "-" + parts[0];
    }

}


package com.sr.simplemanager;

public class Item {
    private int id;
    private String snImei;
    private String ref;
    private String descripcion;
    private String fechaCompra;
    private double precioCoste;
    private String proveedor;
    private double precioVenta;
    private String cliente;

    private String empresa;

    // Constructor, getters y setters
    public Item() {
    }
    public Item(int id, String snImei, String ref, String descripcion, String fechaCompra, double precioCoste, String proveedor, double precioVenta, String cliente,String empresa) {
        this.id = id;
        this.snImei = snImei;
        this.ref = ref;
        this.descripcion = descripcion;
        this.fechaCompra = fechaCompra;
        this.precioCoste = precioCoste;
        this.proveedor = proveedor;
        this.precioVenta = precioVenta;
        this.cliente = cliente;
        this.empresa = empresa;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getSnImei() {
        return snImei;
    }

    public void setSnImei(String snImei) {
        this.snImei = snImei;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public double getPrecioCoste() {
        return precioCoste;
    }

    public void setPrecioCoste(double precioCoste) {
        this.precioCoste = precioCoste;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEmpresa() {
        return empresa;
    }
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}

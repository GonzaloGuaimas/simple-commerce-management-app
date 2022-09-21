package com.example.bellaquita.Objetos;

public class Producto {

    private String Codigo;
    private String Descripcion;
    private String Marca;
    private String Color;
    private String Talle;
    private String PrecioCosto;
    private String PrecioLista;
    private String precioContado;
    private String precio;

    public Producto() {
        //public no-arg constructor needed
    }

    public Producto(String codigo, String descripcion, String marca, String color, String talle, String precioCosto, String precioLista, String precioContado,String precio) {
        Codigo = codigo;
        Descripcion = descripcion;
        Marca = marca;
        Color = color;
        Talle = talle;
        PrecioCosto = precioCosto;
        PrecioLista = precioLista;
        this.precioContado = precioContado;
        this.precio = precio;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getTalle() {
        return Talle;
    }

    public void setTalle(String talle) {
        Talle = talle;
    }

    public String getPrecioCosto() {
        return PrecioCosto;
    }

    public void setPrecioCosto(String precioCosto) {
        PrecioCosto = precioCosto;
    }

    public String getPrecioLista() {
        return PrecioLista;
    }

    public void setPrecioLista(String precioLista) {
        PrecioLista = precioLista;
    }

    public String getPrecioContado() {
        return precioContado;
    }

    public void setPrecioContado(String precioContado) {
        this.precioContado = precioContado;
    }
}

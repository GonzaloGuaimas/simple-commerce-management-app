package com.example.bellaquita.Objetos;

import java.util.ArrayList;

public class Venta {

    private String ID;
    private String fecha;
    private String Hora;
    private int Total;
    private int Ganancia;
    private String Vendedora;
    private String Cliente;
    private ArrayList<Producto> productos;

    public Venta() {
        //public no-arg constructor needed
    }

    public Venta(String ID, String fecha, String hora, int total, int ganancia, String vendedora, ArrayList<Producto> productos,String Cliente) {
        this.ID = ID;
        this.fecha = fecha;
        Hora = hora;
        Total = total;
        Ganancia = ganancia;
        Vendedora = vendedora;
        this.Cliente = Cliente;
        this.productos = productos;
    }

    public String getCliente() {
        return Cliente;
    }

    public void setCliente(String cliente) {
        Cliente = cliente;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public int getGanancia() {
        return Ganancia;
    }

    public void setGanancia(int ganancia) {
        Ganancia = ganancia;
    }

    public String getVendedora() {
        return Vendedora;
    }

    public void setVendedora(String vendedora) {
        Vendedora = vendedora;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }
}

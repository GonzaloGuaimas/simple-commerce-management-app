package com.example.bellaquita.Objetos;

public class Egreso {

    private String ID;
    private String Fecha;
    private String Hora;
    private String Motivo;
    private String Monto;
    private String Vendedora;
    private String Tipo;

    public Egreso() {

    }

    public Egreso(String ID, String fecha, String hora, String motivo, String monto, String Vendedora,String Tipo) {
        this.ID = ID;
        Fecha = fecha;
        Hora = hora;
        Motivo = motivo;
        Monto = monto;
        this.Vendedora = Vendedora;
        this.Tipo = Tipo;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getVendedora() {
        return Vendedora;
    }

    public void setVendedora(String vendedora) {
        Vendedora = vendedora;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public String getMotivo() {
        return Motivo;
    }

    public void setMotivo(String motivo) {
        Motivo = motivo;
    }

    public String getMonto() {
        return Monto;
    }

    public void setMonto(String monto) {
        Monto = monto;
    }
}

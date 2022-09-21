package com.example.bellaquita.Objetos;

public class Estado {
    private boolean actualizado;

    public Estado(){

    }

    public Estado(boolean actualizado){
        setActualizado(actualizado);
    }

    public boolean isActualizado() {
        return actualizado;
    }

    public void setActualizado(boolean actualizado) {
        this.actualizado = actualizado;
    }
}

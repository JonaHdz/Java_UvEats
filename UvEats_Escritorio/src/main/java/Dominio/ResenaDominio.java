package Dominio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class ResenaDominio {
    private int idResena;
    private int idProducto;
    private int idUsuario;

    @SerializedName("resena1")
    private String resena;

   // private LocalDate fecha;

    public ResenaDominio() {}

    public ResenaDominio(int idResena, int idProducto, int idUsuario, String resena) {
        this.idResena = idResena;
        this.idProducto = idProducto;
        this.idUsuario = idUsuario;
        this.resena = resena;
       // this.fecha = fecha;
    }

    public int getIdResena() {
        return idResena;
    }

    public void setIdResena(int idResena) {
        this.idResena = idResena;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getResena() {
        return resena;
    }

    public void setResena(String resena) {
        this.resena = resena;
    }
/*
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }*/
}

/*package Dominio;

import java.time.LocalDateTime;

public class ResenaDominio {
    private int idResena;
    private int idProducto;
    private int idUsuario;
    private String resena1;
    private LocalDateTime fecha ;

    public ResenaDominio(){}

    public ResenaDominio(int idResena, int idProducto, int idUsuario, String resena, LocalDateTime fecha){
        this.idResena = idResena;
        this.idProducto = idProducto;
        this.idUsuario = idUsuario;
        this.resena1 = resena;
        this.fecha = fecha;

    }

    public int getIdResena() {
        return idResena;
    }

    public void setIdResena(int idResena) {
        this.idResena = idResena;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getResena() {
        return resena1;
    }

    public void setResena(String resena) {
        this.resena1 = resena;
    }


    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
*/
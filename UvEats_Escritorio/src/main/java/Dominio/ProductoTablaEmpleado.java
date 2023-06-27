package Dominio;

import javafx.scene.image.Image;

public class ProductoTablaEmpleado {


    private int idProducto ;

    private String nombre ;

    private String descripcion ;

    private double precio ;

    private int idCategoria ;
    private String categoria ;

    private String estadoProducto ;

    private byte[] fotoProducto ;
    private  String fotoProductoString;
    private Image imageProducto;



    public ProductoTablaEmpleado(){}
    public ProductoTablaEmpleado(int idProducto, String nombre, String descripcion , double precio , int idCategoria , String categoria , String estadoProducto , byte[] fotoProducto, String fotoProductoString, Image imageProducto){

        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.idCategoria = idCategoria;
        this.categoria = categoria;
        this.estadoProducto = estadoProducto;
        this.fotoProducto = fotoProducto;

        this.fotoProductoString = fotoProductoString;
        this.imageProducto = imageProducto;
    }
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }


    public byte[] getFotoProducto() {
        return fotoProducto;
    }

    public void setFotoProducto(byte[] fotoProducto) {
        this.fotoProducto = fotoProducto;
    }


    public String getFotoProductoString() {
        return fotoProductoString;
    }

    public void setFotoProductoString(String fotoProductoString) {
        this.fotoProductoString = fotoProductoString;
    }

    public Image getImageProducto() {
        return imageProducto;
    }

    public void setImageProducto(Image imageProducto) {
        this.imageProducto = imageProducto;
    }
}

package Dominio;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProductoTabla {
    private String datos;
    private String fotoCadena;
    private Image fotoUsable;
    private ImageView imagenView;
    private int idProducto;

    private Image imageProducto;
    private String categoria;

    public ProductoTabla(){}
    public ProductoTabla(int idProducto, String datos, String fotoCadena , Image fotoUsable, Image imageProducto, String categoria){
        this.idProducto = idProducto;
        this.datos = datos;
        this.fotoCadena = fotoCadena;
        this.fotoUsable = fotoUsable;
        this.imageProducto = imageProducto;
        this.categoria = categoria;
    }


    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public String getFotoCadena() {
        return fotoCadena;
    }

    public void setFotoCadena(String fotoCadena) {
        this.fotoCadena = fotoCadena;
    }

    public Image getFotoUsable() {
        return fotoUsable;
    }

    public void setFotoUsable(Image fotoUsable) {
        this.fotoUsable = fotoUsable;
    }

    public ImageView getImagenView() {
        return imagenView;
    }

    public void setImagenView(ImageView imagenView) {
        this.imagenView = imagenView;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public Image getImageProducto() {
        return imageProducto;
    }

    public void setImageProducto(Image imageProducto) {
        this.imageProducto = imageProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}

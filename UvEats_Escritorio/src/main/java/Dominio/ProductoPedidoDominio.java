package Dominio;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ProductoPedidoDominio {
    private int idProductoPedido;
    private int idPedido;
    private int idProducto;
    private int cantidad;
    private float subtotal;
    private String estadoProducto;
    private String nombreProducto;

    public ProductoPedidoDominio(){}
    public ProductoPedidoDominio(int idProductoPedido, int idPedido, int idProducto, int cantidad, float subtotal, String estadoProducto, String nombreProducto) {
        this.idProductoPedido = idProductoPedido;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.estadoProducto = estadoProducto;
        nombreProducto = nombreProducto;
    }

    public int getIdProductoPedido() {
        return idProductoPedido;
    }

    public void setIdProductoPedido(int idProductoPedido) {
        this.idProductoPedido = idProductoPedido;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }



    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }
}

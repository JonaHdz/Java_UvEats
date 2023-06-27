package Dominio;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoRecibidoDominio {
    private int idPedido;
    private float total;
    private String estadoPedido;
    private int idUsuario;
    private List<ProductoPedidoDominio> productosPedido ;
    private LocalDateTime fechaPedido;
    private String nombreUsuario;

    public PedidoRecibidoDominio(int idPedido, float total, String estadoPedido, int idUsuario, List<ProductoPedidoDominio> productosPedido, LocalDateTime fechaPedido, String nombreUsuario) {
        this.idPedido = idPedido;
        this.total = total;
        this.estadoPedido = estadoPedido;
        this.idUsuario = idUsuario;
        this.productosPedido = productosPedido;
        this.fechaPedido = fechaPedido;
        this.nombreUsuario = nombreUsuario;
    }
    public PedidoRecibidoDominio() {

    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<ProductoPedidoDominio> getProductosPedido() {
        return productosPedido;
    }

    public void setProductosPedido(List<ProductoPedidoDominio> productosPedido) {
        this.productosPedido = productosPedido;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}

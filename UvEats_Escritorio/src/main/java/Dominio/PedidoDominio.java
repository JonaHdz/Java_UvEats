package Dominio;

import java.util.List;
//calse usada para enviar un pedido(no contiene fecha ya que el servidor la agrega)
public class PedidoDominio {
    private int idPedido;
    private float total;
    private String EstadoPedido;
    private int idUsuario;
    private List<ProductoPedidoDominio> productosPedido ;

    public PedidoDominio(){}

    public PedidoDominio(int idPedido, float total, String estadoPedido, int idUsuario, List<ProductoPedidoDominio> productosPedido) {
        this.idPedido = idPedido;
        this.total = total;
        EstadoPedido = estadoPedido;
        this.idUsuario = idUsuario;
        this.productosPedido = productosPedido;
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
        return EstadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        EstadoPedido = estadoPedido;
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
}

package Dominio;

public class PedidoTabla {
    private int numeroPedido;
    private String pedido;
    private int idCliente;
    private String  cliente;

    public PedidoTabla(int numeroPedido, String pedido, int idCliente, String cliente) {
        this.numeroPedido = numeroPedido;
        this.pedido = pedido;
        this.idCliente = idCliente;
        this.cliente = cliente;

    }

    public PedidoTabla() {

    }

    public int getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(int numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }


    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}

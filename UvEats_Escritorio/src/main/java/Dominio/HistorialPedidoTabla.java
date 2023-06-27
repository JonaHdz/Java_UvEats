package Dominio;

import java.time.LocalDateTime;

public class HistorialPedidoTabla {
    private int idPedido;
    private String pedidoHistorial;
    private double total;
    private LocalDateTime fecha;
    private String estado;

    public HistorialPedidoTabla(){}
    public HistorialPedidoTabla(int idPedido, String pedidoHistorial, double total, LocalDateTime fecha, String estado) {
        this.idPedido = idPedido;
        this.pedidoHistorial = pedidoHistorial;
        this.total = total;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getPedidoHistorial() {
        return pedidoHistorial;
    }

    public void setPedidoHistorial(String pedidoHistorial) {
        this.pedidoHistorial = pedidoHistorial;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

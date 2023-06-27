package Dominio;

public class ConversacionDominio {
    private int idConversacionesPedido;
    private int idPedido;
    private String conversacion;

    public ConversacionDominio(){}
    public ConversacionDominio(int idConversacionesPedido, int idPedido, String conversacion) {
        this.idConversacionesPedido = idConversacionesPedido;
        this.idPedido = idPedido;
        this.conversacion = conversacion;
    }

    public int getIdConversacionesPedido() {
        return idConversacionesPedido;
    }

    public void setIdConversacionesPedido(int idConversacionesPedido) {
        this.idConversacionesPedido = idConversacionesPedido;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getConversacion() {
        return conversacion;
    }

    public void setConversacion(String conversacion) {
        this.conversacion = conversacion;
    }
}

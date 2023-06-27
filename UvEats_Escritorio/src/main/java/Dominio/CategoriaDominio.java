package Dominio;

public class CategoriaDominio {
    private int idCategoria;

    private String categoria ;

    public CategoriaDominio(){}

    public CategoriaDominio(int idCategoria,String categoria)
    {
        this.idCategoria = idCategoria;
        this.categoria = categoria;
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
}

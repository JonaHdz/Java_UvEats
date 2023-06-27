package DominioRespuesta;

import Dominio.CategoriaDominio;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRespuesta {
    private int codigo;
    private List<CategoriaDominio> categoriaList = new ArrayList<>();
    public CategoriaRespuesta(){}
    public CategoriaRespuesta(int codigo, List<CategoriaDominio> categoriaList)
    {
        this.codigo = codigo;
        this.categoriaList = categoriaList;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public List<CategoriaDominio> getCategoriaList() {
        return categoriaList;
    }

    public void setCategoriaList(List<CategoriaDominio> categoriaList) {
        this.categoriaList = categoriaList;
    }
}

package DominioRespuesta;

import Dominio.ResenaDominio;
import Dominio.ResenaRecibidaDominio;

import java.util.ArrayList;
import java.util.List;

public class ResenasRespuesta {
    private int codigo;
    private List<ResenaRecibidaDominio> resenas = new ArrayList<>();

    public ResenasRespuesta(){}
    public ResenasRespuesta(int codigo, List<ResenaRecibidaDominio> resenas){
        this.codigo = codigo;
        this.resenas = resenas;
    }


    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public List<ResenaRecibidaDominio> getResenas() {
        return resenas;
    }

    public void setResenas(List<ResenaRecibidaDominio> resenas) {
        this.resenas = resenas;
    }
}

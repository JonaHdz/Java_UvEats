package DominioRespuesta;

import Dominio.UsuarioDominio;

public class LoginRespuesta {
    private int codigo ;
    private UsuarioDominio usuario = new UsuarioDominio();
    private String token;

    public LoginRespuesta(){}
    public LoginRespuesta(int codigo, UsuarioDominio usuario, String token)
    {
        this.codigo = codigo;
        this.usuario = usuario;
        this.token = token;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public UsuarioDominio getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDominio usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

package Dominio;

public class UsuarioDominio {
    private int idUsuario;

    private String correo ;

    private String contrasena;

    private String nombre;

    private String apellido ;

    private String telefono ;

    private String tipo ;

    private String foto;

    private byte[] fotoBytes;

    public UsuarioDominio(int idUsuario, String correo, String contrasena,  String apellido, String telefono, String tipo, String nombre, String foto, byte[] fotoBytes)
    {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.apellido = apellido;
        this.telefono = telefono;
        this.tipo = tipo;
        this.nombre = nombre;
        this.foto = foto;
        this.fotoBytes = fotoBytes;
    }

    public UsuarioDominio() {

    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }



    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }




    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public byte[] getFotoBytes() {
        return fotoBytes;
    }

    public void setFotoBytes(byte[] fotoBytes) {
        this.fotoBytes = fotoBytes;
    }
}

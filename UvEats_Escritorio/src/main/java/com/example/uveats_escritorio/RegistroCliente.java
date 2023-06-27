package com.example.uveats_escritorio;

import Dominio.UsuarioDominio;
import DominioRespuesta.LoginRespuesta;
import Interfaces.UsuarioInterface;
import Utilidades.*;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RegistroCliente {

    @FXML
    TextField tf_contra;
    @FXML
    TextField tf_correo;
    @FXML
    TextField tf_telefono;
    @FXML
    TextField tf_nombre;
    @FXML
    TextField tf_apellido;

    private boolean edicion = false;
    private LoginRespuesta loginRespuesta;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
            addConverterFactory(GsonConverterFactory.create()).build();
    private UsuarioInterface servicioUsuario = retrofit.create(UsuarioInterface.class);

    public void btn_Registrar(ActionEvent actionEvent) {
        if (!edicion) {
            if (tf_contra.getText().isBlank() || tf_nombre.getText().isBlank() || tf_correo.getText().isBlank())
                mostrarMensaje(Alert.AlertType.ERROR, "Campos obligatorios", "rellenar campos");
            else if (!tf_correo.getText().contains("@estudiantes.uv.mx"))
                mostrarMensaje(Alert.AlertType.ERROR, "correo invalido",
                        "El correo instroducido debe ser del domino @estudiantes.uv.mx");
            else {
                int codigo = ValidarCorreoUnico();
                if (codigo == 200) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Correo existente",
                            "El correo ya fue usado, favor de cambiar");
                } else if (codigo == 404)
                    guardarInformacion();
                else if (codigo == 502)
                    mostrarMensaje(Alert.AlertType.ERROR, "error", "error de conexion");
            }
        } else {
            actualizarDatos();
        }
    }

    private void actualizarDatos() {
        UsuarioDominio usuarioModificado = new UsuarioDominio();
        usuarioModificado.setIdUsuario(loginRespuesta.getUsuario().getIdUsuario());
        usuarioModificado.setCorreo(loginRespuesta.getUsuario().getCorreo());
        usuarioModificado.setContrasena(MetodoCifrado.encriptarCadena(tf_contra.getText()));
        usuarioModificado.setNombre(tf_nombre.getText());
        usuarioModificado.setApellido(tf_apellido.getText());
        usuarioModificado.setTelefono(tf_telefono.getText());
        mandarPeticionActualizacionUsuario(usuarioModificado);
    }

    private void mandarPeticionActualizacionUsuario(UsuarioDominio usuarioModificado) {
        Call<JsonObject> peticion = servicioUsuario.modificarCliente("Bearer " +
                loginRespuesta.getToken(), usuarioModificado);
        try {
            Response respuesta = peticion.execute();
            if (respuesta.isSuccessful()) {
                mostrarMensaje(Alert.AlertType.INFORMATION, "Usuario modificado",
                        "La informacion fue actualizada con exito");
                loginRespuesta.setUsuario(usuarioModificado);
                abrirMenuCLiente();
            } else {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", respuesta.message());
            }
        } catch (Exception e) {
        }
    }

    private void abrirMenuCLiente() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuCliente.fxml"));
        try {
            Parent root = loader.load();
            MenuCliente controladorMenuCliente = loader.getController();
            controladorMenuCliente.cargarUsuario(loginRespuesta);
            controladorMenuCliente.cargarProductos();
            controladorMenuCliente.configurarTabla();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage ventanaEdicon = (Stage) tf_correo.getScene().getWindow();
            ventanaEdicon.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void guardarInformacion() {
        UsuarioDominio nuevoUsuarioDominio = new UsuarioDominio();
        nuevoUsuarioDominio.setNombre(tf_nombre.getText());
        nuevoUsuarioDominio.setApellido(tf_apellido.getText());
        nuevoUsuarioDominio.setContrasena(MetodoCifrado.encriptarCadena(tf_contra.getText()));
        nuevoUsuarioDominio.setCorreo(tf_correo.getText());
        nuevoUsuarioDominio.setTelefono(tf_telefono.getText());
        nuevoUsuarioDominio.setTipo(TipoUsuario.TIPO_CLIENTE);
        nuevoUsuarioDominio.setFoto(null);
        nuevoUsuarioDominio.setFotoBytes(null);
        nuevoUsuarioDominio.setIdUsuario(0);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
                addConverterFactory(GsonConverterFactory.create()).build();
        int codigo = 0;
        UsuarioInterface servicio = retrofit.create(UsuarioInterface.class);
        Call<JsonObject> llamadaPut = servicio.registrarCliente(nuevoUsuarioDominio);
        try {
            Response respuesta = llamadaPut.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = (JsonObject) respuesta.body();
                    codigo = bodyRespuesta.get("codigo").getAsInt();
                    if (codigo == 200) {
                        mostrarMensaje(Alert.AlertType.INFORMATION, "Usuario regitrado",
                                "usuario registrado exitosamente, regresando a inicio de sesion");
                        cerrarVentana();
                    } else {
                        System.out.println("Ocurrio un fallo al registrar al usuario");
                    }
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion: "
                            + respuesta.message() + respuesta.code() + respuesta.errorBody());
                cargarInicioSesion();
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
                cargarInicioSesion();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void cerrarVentana() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("InicioSesion.fxml"));
        try {
            Parent root = loader.load();
            InicioSesionController controladorMenuCliente = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tf_correo.getScene().getWindow();
            login.close();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int ValidarCorreoUnico() {
        int codigo = 0;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
                addConverterFactory(GsonConverterFactory.create()).build();
        UsuarioInterface servicio = retrofit.create(UsuarioInterface.class);
        Call<JsonObject> llamadaPut = servicio.validarusuaro(tf_correo.getText());
        try {
            Response respuesta = llamadaPut.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = (JsonObject) respuesta.body();
                    codigo = bodyRespuesta.get("codigo").getAsInt();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                    cargarInicioSesion();
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
                cargarInicioSesion();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return codigo;
    }

    public void btn_cancelar(ActionEvent actionEvent) {
        if (!edicion)
            cerrarVentana();
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuCliente.fxml"));
            try {
                Parent root = loader.load();
                MenuCliente controladorMenuCliente = loader.getController();
                controladorMenuCliente.cargarUsuario(loginRespuesta);
                controladorMenuCliente.cargarProductos();
                controladorMenuCliente.configurarTabla();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage login = (Stage) tf_correo.getScene().getWindow();
                login.close();
                stage.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public void CargarEdicion(LoginRespuesta loginRespuesta, boolean edicion) {
        this.edicion = edicion;
        this.loginRespuesta = loginRespuesta;
        loginRespuesta.getUsuario().setContrasena(MetodoCifrado.
                desencriptarCadena(loginRespuesta.getUsuario().getContrasena()));
        tf_nombre.setText(loginRespuesta.getUsuario().getNombre());
        tf_apellido.setText(loginRespuesta.getUsuario().getApellido());
        tf_correo.setText(loginRespuesta.getUsuario().getCorreo());
        tf_contra.setText(loginRespuesta.getUsuario().getContrasena());
        tf_telefono.setText(loginRespuesta.getUsuario().getTelefono());
        tf_correo.setDisable(true);
    }

    private void cargarInicioSesion() {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("InicioSesion.fxml"));
            try {
                Parent root = loader.load();
                InicioSesionController controlador = loader.getController();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage stageActual = (Stage) tf_correo.getScene().getWindow();
                stageActual.close();
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
}

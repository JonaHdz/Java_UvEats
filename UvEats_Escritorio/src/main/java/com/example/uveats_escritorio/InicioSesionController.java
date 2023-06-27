package com.example.uveats_escritorio;

import Dominio.LoginDominio;
import Dominio.UsuarioDominio;
import DominioRespuesta.LoginRespuesta;
import Interfaces.LoginInterface;
import Utilidades.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class InicioSesionController {
    @FXML
    private TextField tf_correo;
    @FXML
    private TextField tf_contrasena;
    private UsuarioDominio usuarioDominioConvertido;
    private LoginRespuesta loginRespuesta;


    public void btn_IniciarSesion(ActionEvent actionEvent) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
                addConverterFactory(GsonConverterFactory.create()).build();
        LoginInterface servicio = retrofit.create(LoginInterface.class);
        LoginDominio datosSesion = new LoginDominio();
        datosSesion.setCorreo(tf_correo.getText());
        datosSesion.setContrasena(MetodoCifrado.encriptarCadena(tf_contrasena.getText()));
        Call<JsonObject> llamadaPut = servicio.iniciarSesion(datosSesion);
        try {
            Response<JsonObject> respuesta = llamadaPut.execute();
            if (respuesta.code() == CodigoOperacion.EXITO) {
                JsonObject body = (JsonObject) respuesta.body();
                int codigo = body.get("codigo").getAsInt();
                if (codigo == 200) {
                    JsonObject bodyRespuesta = (JsonObject) respuesta.body();
                    JsonObject usuario = (JsonObject) bodyRespuesta.get("usuario");
                    String token = bodyRespuesta.get("token").getAsString();
                    Gson gson = new GsonBuilder().create();
                    loginRespuesta = new LoginRespuesta();
                    loginRespuesta.setUsuario(gson.fromJson(usuario, new TypeToken<UsuarioDominio>() {
                    }.getType()));
                    loginRespuesta.setCodigo(codigo);
                    loginRespuesta.setToken(token);
                    ComprobacionTipoUsuario();
                } else if (codigo == 404) {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Usuario o contraseño incorrecto. Favor de verificar");
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void ComprobacionTipoUsuario() {
        if (loginRespuesta.getUsuario().getTipo().equals(TipoUsuario.TIPO_CLIENTE))
            abrirVentanaMenuCliente();
        else if (loginRespuesta.getUsuario().getTipo().equals(TipoUsuario.TIPO_EMPLEADO))
            abrirVentanaMenuEmpleado();
        else if (loginRespuesta.getUsuario().getTipo().equals(TipoUsuario.TIPO_ADMIN))
            abrirVentanaMenuAdministrador();
    }

    private void abrirVentanaMenuAdministrador() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuAdministrador.fxml"));
        try {
            Parent root = loader.load();
            MenuAdministradorController menuAdministradorController = loader.getController();
            menuAdministradorController.cargarCredenciales(loginRespuesta);
            if (menuAdministradorController.accesoValido) {
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage login = (Stage) tf_contrasena.getScene().getWindow();
                login.close();
                stage.showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void abrirVentanaMenuEmpleado() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuEmpleado.fxml"));
        try {
            Parent root = loader.load();
            MenuEmpleadoController controladorMenuEmpleado = loader.getController();
            controladorMenuEmpleado.cargarCredenciales(loginRespuesta);
            if (controladorMenuEmpleado.accesoValido) {
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage login = (Stage) tf_contrasena.getScene().getWindow();
                login.close();
                stage.showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void abrirVentanaMenuCliente() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuCliente.fxml"));
        try {
            Parent root = loader.load();
            MenuCliente controladorMenuCliente = loader.getController();
            controladorMenuCliente.cargarUsuario(loginRespuesta);
            controladorMenuCliente.cargarProductos();
            controladorMenuCliente.configurarTabla();
            if (controladorMenuCliente.accesoValido) {
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage login = (Stage) tf_contrasena.getScene().getWindow();
                login.close();
                stage.showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_Resgistrarse(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroCliente.fxml"));
        try {
            Parent root = loader.load();
            RegistroCliente controladorRegistroClien = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tf_contrasena.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

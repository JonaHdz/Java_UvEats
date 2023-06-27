package com.example.uveats_escritorio;

import Dominio.ConversacionDominio;
import Dominio.UsuarioDominio;
import DominioRespuesta.LoginRespuesta;
import Interfaces.ConversacionInterface;
import Interfaces.PedidoInterface;
import Utilidades.CodigoOperacion;
import Utilidades.Mensaje;
import Utilidades.UrlPuertos;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class AvisoCancelacionPedidoController {
    @FXML
    private TextArea tf_motivo;
    @FXML
    private TextField tf_nombreCliente;
    @FXML
    private TextField tf_correo;
    @FXML
    private TextField tf_idPedido;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).addConverterFactory(GsonConverterFactory.create()).build();
    private PedidoInterface servicioPedido = retrofit.create(PedidoInterface.class);
    private ConversacionInterface servicioConversacion = retrofit.create(ConversacionInterface.class);
    private ConversacionDominio conversacion;
    private LoginRespuesta credenciales;
    private UsuarioDominio usuario;
    private int idPedido;

    public void btn_cancelarPedido(ActionEvent actionEvent) {

        if (!tf_motivo.getText().isBlank()) {
            Alert mensajeCerrarSesion = new Alert(Alert.AlertType.CONFIRMATION);
            mensajeCerrarSesion.setTitle("Ultimo paso para cancelacion");
            mensajeCerrarSesion.setHeaderText(null);
            mensajeCerrarSesion.setContentText("Está seguro que sea cancelar el pedido?, " +
                    "una vez cancelado no se podra deshacer");
            ButtonType resultadoBoton = mensajeCerrarSesion.showAndWait().orElse(ButtonType.CANCEL);
            if (resultadoBoton == ButtonType.OK) {
                cancelarPedido();
            }
        } else
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error", "Para cancelar un pedido favor de indicar el motivo");
    }

    private void cancelarPedido() {
        Call<JsonObject> llamadaCancelacionPedido = servicioPedido.cancelarPedido("Bearer " +
                credenciales.getToken(), idPedido);
        try {
            Response<JsonObject> repuesta = llamadaCancelacionPedido.execute();
            if (repuesta.code() != CodigoOperacion.ACCESO_DENEGADO && repuesta.code() != CodigoOperacion.SIN_PERMISO) {
                if (repuesta.isSuccessful()) {
                    Mensaje.mostrarMensaje(Alert.AlertType.INFORMATION, "Pedido cancelado",
                            "El pedido fue cancelado con exito");
                    mandarMensajeCleinte();
                    abrirMenuEmpleado();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Catch", e.getMessage());
        }
    }

    private void mandarMensajeCleinte() {
        conversacion = new ConversacionDominio();
        conversacion.setConversacion("AVISO DE SISTEMA - Su pedido con numero de identificacion " +
                idPedido + " fue cancelado debido al siguiente motivo: " + tf_motivo.getText());
        conversacion.setIdConversacionesPedido(idPedido);
        Call<JsonObject> llamadaEnviarMensaje = servicioConversacion.enviarConversacion("Bearer " +
                credenciales.getToken(), conversacion);
        try {
            Response<JsonObject> respuesta = llamadaEnviarMensaje.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    //no se necesita realizar ningunga operacion para avisar
                } else{
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println("CATCH ENVAR " + e.getMessage());
        }
    }

    private void abrirMenuEmpleado() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuEmpleado.fxml"));
        try {
            Parent root = loader.load();
            MenuEmpleadoController controladorMenuEmpleado = loader.getController();
            controladorMenuEmpleado.cargarCredenciales(credenciales);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tf_nombreCliente.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cargarCredenciales(LoginRespuesta credenciales, UsuarioDominio usuario, int idPedido) {
        this.credenciales = credenciales;
        this.idPedido = idPedido;
        this.usuario = usuario;
        cargarInfoCliente();
    }

    private void cargarInfoCliente() {
        tf_idPedido.setText("" + idPedido);
        tf_correo.setText(usuario.getCorreo());
        tf_nombreCliente.setText(usuario.getNombre() + " " + usuario.getApellido());
    }

    public void btn_regresar(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AtenderPedido.fxml"));
        try {
            Parent root = loader.load();
            AtenderPedidoController atenderPedidoController = loader.getController();
            atenderPedidoController.cargarCredenciales(credenciales, usuario.getIdUsuario(), idPedido);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage stageActual = (Stage) tf_idPedido.getScene().getWindow();
            stageActual.close();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

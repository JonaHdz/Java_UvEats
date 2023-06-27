package com.example.uveats_escritorio;

import Dominio.ConversacionDominio;
import Dominio.PedidoRecibidoDominio;
import Dominio.ProductoPedidoDominio;
import Dominio.UsuarioDominio;
import DominioRespuesta.LoginRespuesta;
import Interfaces.ConversacionInterface;
import Interfaces.PedidoInterface;
import Interfaces.UsuarioInterface;
import Utilidades.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AtenderPedidoController {

    @FXML
    private TableView tb_pedidos;
    @FXML
    private TextField tf_nombre;
    @FXML
    private TextField tf_correo;
    @FXML
    private TextField tf_telefono;
    @FXML
    private TextField tf_escrituraMensaje;
    @FXML
    public TextArea tf_chat;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).addConverterFactory(GsonConverterFactory.create()).build();
    private UsuarioInterface servicioUsuarioInterface = retrofit.create(UsuarioInterface.class);
    private PedidoInterface servicioPedido = retrofit.create(PedidoInterface.class);
    private ConversacionInterface servicioConversacion = retrofit.create(ConversacionInterface.class);
    private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private List<PedidoRecibidoDominio> pedidosList = new ArrayList<>();
    private UsuarioDominio cliente = new UsuarioDominio();
    private int idPedido;
    private LoginRespuesta credenciales;
    public ConversacionDominio conversacion = new ConversacionDominio();
    private ProcesoChatEmpleado procesoChatEmpleado;
    private PedidoRecibidoDominio pedido;
    private List<ProductoPedidoDominio> productosPedidoList = new ArrayList<>();
    private ObservableList pedidoObservable;


    public void cargarCredenciales(LoginRespuesta credenciales, int idCliente, int idPedido) {
        this.credenciales = credenciales;
        this.idPedido = idPedido;
        cargarInformacionUsuario(idCliente);
        cargarPedidos();
        filtrarPedido();
        cargarTablaPedido();
        procesoChatEmpleado = new ProcesoChatEmpleado(credenciales, idPedido, this);
        procesoChatEmpleado.start();
    }


    private void cargarPedidos() {
        Call<JsonObject> llamdaGet = servicioPedido.recuperarPedidosEmpleado("Bearer " +
                credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamdaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() != CodigoOperacion.
                    ACCESO_DENEGADO) {
                JsonObject body = respuesta.body();
                int codigo = body.get("codigo").getAsInt();
                if (codigo == CodigoOperacion.EXITO) {
                    JsonArray listaPedidosJson = (JsonArray) body.get("pedidosRecuperados");
                    System.out.println(listaPedidosJson);
                    pedidosList = gson.fromJson(listaPedidosJson, new TypeToken<List<PedidoRecibidoDominio>>() {
                    }.getType());
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println("Excepcion " + e);
        }
    }

    private void filtrarPedido() {
        for (var util : pedidosList) {
            if (util.getIdPedido() == idPedido) {
                pedido = util;
                productosPedidoList = util.getProductosPedido();
                break;
            }
        }
    }

    private void cargarTablaPedido() {
        tb_pedidos.getColumns().removeAll();
        TableColumn<ProductoPedidoDominio, String> colCompra = new TableColumn<>("Compra");
        colCompra.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCompra.setPrefWidth(200);
        colCompra.setStyle("-fx-font-size: 20px;");
        TableColumn<ProductoPedidoDominio, String> colSubtotal = new TableColumn<>("subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotal.setPrefWidth(100);
        colSubtotal.setStyle("-fx-font-size: 20px;");
        TableColumn<ProductoPedidoDominio, String> colCantidad = new TableColumn<>("cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setPrefWidth(100);
        colCantidad.setStyle("-fx-font-size: 20px;");
        TableColumn<ProductoPedidoDominio, String> colEstado = new TableColumn<>("Estatus");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoProducto"));
        colEstado.setPrefWidth(150);
        colEstado.setStyle("-fx-font-size: 20px;");
        System.out.println("" + productosPedidoList.get(0).getEstadoProducto());
        tb_pedidos.getColumns().addAll(colCompra, colSubtotal, colCantidad, colEstado);
        pedidoObservable = FXCollections.observableArrayList(productosPedidoList);
        tb_pedidos.setItems(pedidoObservable);
    }

    private void cargarInformacionUsuario(int idCliente) {
        Call<JsonObject> llamadaPost = servicioUsuarioInterface.RecuperarClienteId("Bearer " +
                credenciales.getToken(), idCliente);
        try {
            Response<JsonObject> respuesta = llamadaPost.execute();
            if (respuesta.code() != CodigoOperacion.ACCESO_DENEGADO && respuesta.code() != CodigoOperacion.
                    SIN_PERMISO) {
                JsonObject body = respuesta.body();
                int codigo = body.get("codigo").getAsInt();
                if (codigo == CodigoOperacion.EXITO) {
                    JsonObject clienteJson = (JsonObject) body.get("cliente");
                    System.out.println(clienteJson);
                    Gson gson = new GsonBuilder().create();
                    cliente = gson.fromJson(clienteJson, new TypeToken<UsuarioDominio>() {
                    }.getType());
                    tf_nombre.setText(cliente.getNombre() + " " + cliente.getApellido());
                    tf_telefono.setText(cliente.getTelefono());
                    tf_correo.setText(cliente.getCorreo());
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
            } else
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");

        } catch (Exception e) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "CATCH", e.getMessage());
        }

    }


    public void btn_eliminar(ActionEvent actionEvent) {
        if (pedidoObservable.size() == 1) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error",
                    "Debido a que el pedido solo cuenta con un prodcuto, es necesario Cancelar el pedido");
        } else {
            if (tb_pedidos.getSelectionModel().getSelectedIndex() >= 0) {
                Alert mensajeCerrarSesion = new Alert(Alert.AlertType.CONFIRMATION);
                mensajeCerrarSesion.setTitle("Cancelar producto");
                mensajeCerrarSesion.setHeaderText(null);
                mensajeCerrarSesion.setContentText("Está a punto de cancelar un producto del pedido, " +
                        "está seguro?, asegurece de tener la confirmacion del cliente antes de realiar " +
                        "esta operacion");
                ButtonType resultadoBoton = mensajeCerrarSesion.showAndWait().orElse(ButtonType.CANCEL);
                if (resultadoBoton == ButtonType.OK) {
                    cancelarProductoPedido();
                }
            } else
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error", "Para cancelar un " +
                        "producto del pedido, favor de seleccionarlo de la tabla");
        }
    }

    private void cancelarProductoPedido() {
        ProductoPedidoDominio seleccion = (ProductoPedidoDominio) tb_pedidos.getSelectionModel().getSelectedItem();
        Mensaje.mostrarMensaje(Alert.AlertType.INFORMATION, "", "id" + seleccion.getIdProductoPedido());
        Call<JsonObject> llamadaPut = servicioPedido.cancelarProductoPedido("Bearer " + credenciales.getToken(), seleccion.getIdProductoPedido());
        try {
            Response<JsonObject> respuesta = llamadaPut.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    Mensaje.mostrarMensaje(Alert.AlertType.INFORMATION, "Producto cancelado",
                            "El prodcuto fue cancelado del pedido");
                    cargarPedidos();
                    filtrarPedido();
                    pedidoObservable.clear();
                    pedidoObservable = FXCollections
                            .observableArrayList(productosPedidoList);
                    tb_pedidos.setItems(pedidoObservable);
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error",
                            "Error de conexion" + respuesta.message());

            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR",
                        "No se cuenta con el acceso");

            }

        } catch (Exception e) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "CATCH", "Cach: " + e.getMessage());
        }
    }

    public void btn_enviarMensaje(ActionEvent actionEvent) {
        if (!tf_escrituraMensaje.getText().isBlank()) {
            conversacion.setConversacion(conversacion.getConversacion() + "\nEmpleado: " +
                    tf_escrituraMensaje.getText());
            Call<JsonObject> llamadaEnviarMensaje = servicioConversacion.enviarConversacion("Bearer " +
                    credenciales.getToken(), conversacion);
            try {
                Response<JsonObject> respuesta = llamadaEnviarMensaje.execute();
                if (respuesta.isSuccessful()) {
                    tf_escrituraMensaje.clear();
                } else
                    System.out.println("error al enviar mensaje " + respuesta.message());
            } catch (Exception e) {
                System.out.println("CATCH ENVAR " + e.getMessage());
            }
        }
    }

    public void btn_PedidoRecogido(ActionEvent actionEvent) {
        Alert mensajeCerrarSesion = new Alert(Alert.AlertType.CONFIRMATION);
        mensajeCerrarSesion.setTitle("Pedido recogido");
        mensajeCerrarSesion.setHeaderText(null);
        mensajeCerrarSesion.setContentText("Está seguro de marca el pedido como recogido? asegurece de " +
                "entregar el pedido antes de aceptar");
        ButtonType resultadoBoton = mensajeCerrarSesion.showAndWait().orElse(ButtonType.CANCEL);
        if (resultadoBoton == ButtonType.OK) {
            Call<JsonObject> llamadaRecogerPedido = servicioPedido.recogerPedido("Bearer " +
                    credenciales.getToken(), idPedido);
            try {
                Response<JsonObject> respuesta = llamadaRecogerPedido.execute();
                if (respuesta.isSuccessful()) {
                    Mensaje.mostrarMensaje(Alert.AlertType.INFORMATION, "pedido entregado",
                            "El pedido fue marcado como entregado");
                    procesoChatEmpleado.detenerHilo();
                    abrirMenuEmpleado();
                }
            } catch (Exception e) {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "CATCH", e.getMessage());
            }
        }
    }

    public void btn_CancelarPedido(ActionEvent actionEvent) {

        Alert mensajeCerrarSesion = new Alert(Alert.AlertType.CONFIRMATION);
        mensajeCerrarSesion.setTitle("Cancelar pedido");
        mensajeCerrarSesion.setHeaderText(null);
        mensajeCerrarSesion.setContentText("Está seguro que sea cancelar el pedido?");
        ButtonType resultadoBoton = mensajeCerrarSesion.showAndWait().orElse(ButtonType.CANCEL);
        if (resultadoBoton == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvisoCancelacionPedido.fxml"));
            try {
                procesoChatEmpleado.detenerHilo();
                Parent root = loader.load();
                AvisoCancelacionPedidoController avisoCancelacionPedidoController = loader.getController();
                avisoCancelacionPedidoController.cargarCredenciales(credenciales, cliente, idPedido);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage stageActual = (Stage) tf_chat.getScene().getWindow();
                stageActual.close();
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btn_Regresar(ActionEvent actionEvent) {
        procesoChatEmpleado.detenerHilo();
        abrirMenuEmpleado();
    }

    private void abrirMenuEmpleado() {
        if (procesoChatEmpleado.isAlive() || procesoChatEmpleado != null) {
            procesoChatEmpleado.detenerHilo();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuEmpleado.fxml"));
        try {
            Parent root = loader.load();
            MenuEmpleadoController controladorMenuEmpleado = loader.getController();
            controladorMenuEmpleado.cargarCredenciales(credenciales);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tf_chat.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



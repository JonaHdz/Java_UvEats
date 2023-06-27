package com.example.uveats_escritorio;

import Dominio.ConversacionDominio;
import Dominio.PedidoRecibidoDominio;
import Dominio.PedidoTabla;
import DominioRespuesta.LoginRespuesta;
import Interfaces.ConversacionInterface;
import Interfaces.PedidoInterface;
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

public class NotificacionesCliente {

    @FXML
    public TextField tf_cuadroMensaje;
    @FXML
    public TableView tb_pedidos;
    @FXML
    public TextArea tf_chat;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
            addConverterFactory(GsonConverterFactory.create()).build();
    private PedidoInterface servicioPedido = retrofit.create(PedidoInterface.class);
    private ConversacionInterface servicioConversacion = retrofit.create(ConversacionInterface.class);
    public ConversacionDominio conversacion = new ConversacionDominio();
    private LoginRespuesta credenciales;
    private int idCliente;
    private List<PedidoRecibidoDominio> pedidosList = new ArrayList<>();
    private List<PedidoTabla> pedidosTablaList = new ArrayList<>();
    private ObservableList<PedidoTabla> pedidosObservable = FXCollections.observableArrayList(pedidosTablaList);
    private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private ProcesoChatCliente procesoChatCliente;
    private PedidoTabla seleccion;

    public void cargarCredenciales(LoginRespuesta credenciales, int idCliente) {
        this.credenciales = credenciales;
        this.procesoChatCliente = new ProcesoChatCliente(credenciales, this);
        cargarPedidos();
        agregarListenerTabla();
    }

    private void agregarListenerTabla() {
        tb_pedidos.setOnMouseClicked(event -> {
            if (procesoChatCliente.isAlive())
                procesoChatCliente.detenerHilo();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (event.getClickCount() == 1) {
                seleccion = (PedidoTabla) tb_pedidos.getSelectionModel().getSelectedItem();
                recuperarChat();
            }
        });
    }

    private void recuperarChat() {
        if (procesoChatCliente == null || !procesoChatCliente.isAlive()) {
            procesoChatCliente = new ProcesoChatCliente(credenciales, this);
            procesoChatCliente.setIdPedido(seleccion.getNumeroPedido());
            procesoChatCliente.start();
        }
    }

    private void cargarPedidos() {
        Call<JsonObject> llamdaGet = servicioPedido.recuperarPedidosActualesCliente("Bearer " +
                credenciales.getToken(), credenciales.getUsuario().getIdUsuario());
        try {
            Response<JsonObject> respuesta = llamdaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject body = respuesta.body();
                    JsonArray listaPedidosJson = (JsonArray) body.get("pedidosRecuperados");
                    System.out.println(listaPedidosJson);
                    pedidosList = gson.fromJson(listaPedidosJson, new TypeToken<List<PedidoRecibidoDominio>>() {
                    }.getType());
                    configurartabla();
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

    private void configurartabla() {
        TableColumn<PedidoTabla, String> colNumeroPedido = new TableColumn<>("Numero Pedido");
        colNumeroPedido.setCellValueFactory(new PropertyValueFactory<>("NumeroPedido"));
        colNumeroPedido.setPrefWidth(100);
        colNumeroPedido.setStyle("-fx-font-size: 20px;");
        TableColumn<PedidoTabla, String> colPedidoDescripcion = new TableColumn<>("Pedido");
        colPedidoDescripcion.setCellValueFactory(new PropertyValueFactory<>("pedido"));
        colPedidoDescripcion.setPrefWidth(350);
        colPedidoDescripcion.setStyle("-fx-font-size: 20px;");
        tb_pedidos.getColumns().addAll(colNumeroPedido, colPedidoDescripcion);
        agregarDatosTabla();
    }

    private void agregarDatosTabla() {
        for (var util : pedidosList) {
            PedidoTabla pedidoTablaTemp = new PedidoTabla();
            pedidoTablaTemp.setIdCliente(util.getIdUsuario());
            pedidoTablaTemp.setCliente(util.getNombreUsuario());
            pedidoTablaTemp.setNumeroPedido(util.getIdPedido());
            String productos = "";
            for (var productosPedido : util.getProductosPedido()) {
                productos += productosPedido.getNombreProducto() + " (" + productosPedido.getCantidad() +
                        " unidad(es))";
            }
            pedidoTablaTemp.setPedido(productos);
            pedidosObservable.add(pedidoTablaTemp);
        }
        tb_pedidos.setItems(pedidosObservable);
    }

    public void btn_enviarMensaje(ActionEvent actionEvent) {
        if (!tf_cuadroMensaje.getText().isBlank()) {
            conversacion.setConversacion(conversacion.getConversacion() + "\nCliente: " +
                    tf_cuadroMensaje.getText());
            Call<JsonObject> llamadaEnviarMensaje = servicioConversacion.enviarConversacion("Bearer " +
                    credenciales.getToken(), conversacion);
            try {
                Response<JsonObject> respuesta = llamadaEnviarMensaje.execute();
                if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                        CodigoOperacion.ACCESO_DENEGADO) {
                    if (respuesta.isSuccessful()) {
                        tf_cuadroMensaje.clear();
                    } else
                        Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
                }
            } catch (Exception e) {
                System.out.println("CATCH ENVAR " + e.getMessage());
            }
        }
    }

    public void btn_regresar(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuCliente.fxml"));
        try {
            Parent root = loader.load();
            MenuCliente menuCliente = loader.getController();
            menuCliente.cargarUsuario(credenciales);
            menuCliente.cargarProductos();
            menuCliente.configurarTabla();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tb_pedidos.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

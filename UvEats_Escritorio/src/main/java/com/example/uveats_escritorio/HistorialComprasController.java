package com.example.uveats_escritorio;

import Dominio.HistorialPedidoTabla;
import Dominio.PedidoRecibidoDominio;
import DominioRespuesta.LoginRespuesta;
import Interfaces.PedidoInterface;
import Utilidades.CodigoOperacion;
import Utilidades.LocalDateTimeAdapter;
import Utilidades.Mensaje;
import Utilidades.UrlPuertos;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

public class HistorialComprasController {
    @FXML
    private RadioButton rb_fecha;
    @FXML
    private DatePicker dp_fecha;
    @FXML
    private TableView tb_historial;

    public boolean accesoValido = true;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).addConverterFactory(GsonConverterFactory.create()).build();
    private PedidoInterface servicioPedido = retrofit.create(PedidoInterface.class);
    private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private List<PedidoRecibidoDominio> pedidosList = new ArrayList<>();
    private LoginRespuesta credenciales;
    private List<HistorialPedidoTabla> historialList = new ArrayList<>();
    private Boolean rbPresionado = false;
    private ObservableList<HistorialPedidoTabla> historialObsevable = FXCollections.observableArrayList(historialList);

    public void cargarCredenciales(LoginRespuesta credenciales) {
        this.credenciales = credenciales;
        cargarHistorial();
    }

    private void cargarHistorial() {
        Call<JsonObject> llamdaGet = servicioPedido.recuperarHistorialCliente("Bearer " +
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
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Erro de conexion");
                    accesoValido = false;
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
                accesoValido = false;
            }
        } catch (Exception e) {
            System.out.println("Excepcion " + e);
        }
    }

    private void configurartabla() {
        TableColumn<HistorialPedidoTabla, String> colNumeroPedido = new TableColumn<>("Numero Pedido");
        colNumeroPedido.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colNumeroPedido.setPrefWidth(100);
        TableColumn<HistorialPedidoTabla, String> colPedido = new TableColumn<>("Pedido");
        colPedido.setCellValueFactory(new PropertyValueFactory<>("pedidoHistorial"));
        colPedido.setPrefWidth(100);
        TableColumn<HistorialPedidoTabla, String> colTotal = new TableColumn<>("total $");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setPrefWidth(100);
        TableColumn<HistorialPedidoTabla, String> colFecha = new TableColumn<>("fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setPrefWidth(100);
        TableColumn<HistorialPedidoTabla, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setPrefWidth(100);
        tb_historial.getColumns().addAll(colNumeroPedido, colPedido, colTotal, colFecha, colEstado);
        agregarDatosTabla();
    }

    private void agregarDatosTabla() {
        for (var util : pedidosList) {
            HistorialPedidoTabla pedidoHistorial = new HistorialPedidoTabla();
            pedidoHistorial.setIdPedido(util.getIdPedido());
            pedidoHistorial.setEstado(util.getEstadoPedido());

            pedidoHistorial.setFecha(util.getFechaPedido());
            pedidoHistorial.setTotal(util.getTotal());
            String productos = "";
            for (var productoPedido : util.getProductosPedido()) {
                productos += productoPedido.getNombreProducto() + "-" + productoPedido.getCantidad() + " X " + productoPedido.getSubtotal() + "\n";
            }
            pedidoHistorial.setPedidoHistorial(productos);
            historialObsevable.add(pedidoHistorial);
        }
        tb_historial.setItems(historialObsevable);
        filtroFecha();
    }

    public void filtroFecha() {
        FilteredList<HistorialPedidoTabla> listaFiltrada = new FilteredList<>(historialObsevable);
        tb_historial.setItems(listaFiltrada);
        dp_fecha.valueProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(a -> {
                if (dp_fecha.getValue() == null) {
                    return true;
                }
                LocalDateTime fechaSeleccionada = dp_fecha.getValue().atStartOfDay();
                return a.getFecha().isEqual(fechaSeleccionada);
            });
        });
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
            Stage login = (Stage) tb_historial.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_rbFecha(ActionEvent actionEvent) {
        if (rbPresionado == false) {
            rbPresionado = true;
            dp_fecha.setDisable(false);
            filtroFecha();
        } else {
            rbPresionado = false;
            dp_fecha.setDisable(true);
            dp_fecha.setValue(null);
            filtroFecha();
        }
    }
}

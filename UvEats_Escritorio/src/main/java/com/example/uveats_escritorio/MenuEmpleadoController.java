package com.example.uveats_escritorio;

import Dominio.PedidoRecibidoDominio;
import Dominio.PedidoTabla;
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

public class MenuEmpleadoController {
    @FXML
    private Label lb_usuario;
    @FXML
    private TableView tb_pedidos;
    private LoginRespuesta credenciales;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).addConverterFactory(GsonConverterFactory.create()).build();
    private PedidoInterface servicioPedido = retrofit.create(PedidoInterface.class);
    private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private List<PedidoTabla> pedidosTablaList = new ArrayList<>();
    private List<PedidoRecibidoDominio> pedidosList = new ArrayList<>();
    private ObservableList<PedidoTabla> pedidosObservable = FXCollections.observableArrayList(pedidosTablaList);

    public boolean accesoValido = true;

    public void cargarCredenciales(LoginRespuesta loginRespuesta) {
        credenciales = loginRespuesta;
        lb_usuario.setText(credenciales.getUsuario().getNombre() + " " + credenciales.getUsuario().getApellido());
        cargarPedidos();
    }

    private void cargarPedidos() {
        Call<JsonObject> llamdaGet = servicioPedido.recuperarPedidosEmpleado("Bearer " +
                credenciales.getToken());
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
                    for (var x : pedidosList)
                        System.out.println();
                    configurartabla();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
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
        TableColumn<PedidoTabla, String> colNumeroPedido = new TableColumn<>("Numero Pedido");
        colNumeroPedido.setCellValueFactory(new PropertyValueFactory<>("NumeroPedido"));
        colNumeroPedido.setPrefWidth(100);
        colNumeroPedido.setStyle("-fx-font-size: 20px;");
        TableColumn<PedidoTabla, String> colPedidoDescripcion = new TableColumn<>("Pedido");
        colPedidoDescripcion.setCellValueFactory(new PropertyValueFactory<>("pedido"));
        colPedidoDescripcion.setPrefWidth(350);
        colPedidoDescripcion.setStyle("-fx-font-size: 20px;");
        TableColumn<PedidoTabla, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colCliente.setStyle("-fx-font-size: 20px;");
        colCliente.setPrefWidth(200);
        tb_pedidos.getColumns().addAll(colNumeroPedido, colPedidoDescripcion, colCliente);
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

    public void btn_cerrarSesion(ActionEvent actionEvent) {
        Alert mensajeCerrarSesion = new Alert(Alert.AlertType.CONFIRMATION);
        mensajeCerrarSesion.setTitle("Cerrar sesión");
        mensajeCerrarSesion.setHeaderText(null);
        mensajeCerrarSesion.setContentText("Esta seguro de cerrar sesión?");
        ButtonType resultadoBoton = mensajeCerrarSesion.showAndWait().orElse(ButtonType.CANCEL);
        if (resultadoBoton == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InicioSesion.fxml"));
            try {
                Parent root = loader.load();
                InicioSesionController controlador = loader.getController();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage stageActual = (Stage) tb_pedidos.getScene().getWindow();
                stageActual.close();
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btn_atenderPedido(ActionEvent actionEvent) {
        if (tb_pedidos.getSelectionModel().getSelectedIndex() >= 0) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AtenderPedido.fxml"));
            try {
                PedidoTabla seleccion = (PedidoTabla) tb_pedidos.getSelectionModel().getSelectedItem();
                Parent root = loader.load();
                AtenderPedidoController atenderPedidoController = loader.getController();
                atenderPedidoController.cargarCredenciales(credenciales, seleccion.getIdCliente(),
                        seleccion.getNumeroPedido());
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage stageActual = (Stage) tb_pedidos.getScene().getWindow();
                stageActual.close();
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error", "Para abrir un pedido, favor de seleccionar un elemento de la tabla");
    }

    public void btn_gestionProductos(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GestionPlatillos.fxml"));
            Parent root = loader.load();
            GestionPlatillosCntroller controladorResenasPlatilloController = loader.getController();
            controladorResenasPlatilloController.cargarCredenciales(credenciales);
            controladorResenasPlatilloController.cargarProductos();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage menu = (Stage) tb_pedidos.getScene().getWindow();
            menu.close();
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void btn_GestionCategorias(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GestionCategorias.fxml"));
            Parent root = loader.load();
            GestionCategoriasController gestionCategoriasController = loader.getController();
            gestionCategoriasController.cargarCredenciales(credenciales);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage menu = (Stage) tb_pedidos.getScene().getWindow();
            menu.close();
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

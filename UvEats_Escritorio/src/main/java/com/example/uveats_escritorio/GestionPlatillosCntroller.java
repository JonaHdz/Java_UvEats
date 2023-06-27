package com.example.uveats_escritorio;

import Dominio.ProductoDominio;
import Dominio.ProductoTablaEmpleado;
import DominioRespuesta.LoginRespuesta;
import Interfaces.ProductoInterface;
import Utilidades.CodigoOperacion;
import Utilidades.EstadoProducto;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class GestionPlatillosCntroller {

    @FXML
    private TableView tb_productos;
    private LoginRespuesta credenciales;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
            addConverterFactory(GsonConverterFactory.create()).build();
    private ProductoInterface servicioProducto = retrofit.create(ProductoInterface.class);
    private List<ProductoTablaEmpleado> listaProductoDominios = new ArrayList<>();
    private List<ProductoDominio> productosrecuperados = new ArrayList<>();

    public void cargarCredenciales(LoginRespuesta credenciales) {
        this.credenciales = credenciales;
    }

    public void cargarProductos() {
        listaProductoDominios.clear();
        Call<JsonObject> llamadaGet = servicioProducto.recuperarProductosEmpleado("Bearer " +
                credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = respuesta.body();
                    JsonArray listaProductosJson = (JsonArray) bodyRespuesta.get("productosRecuperados");
                    Gson gson = new GsonBuilder().create();
                    productosrecuperados = gson.fromJson(listaProductosJson, new TypeToken<List<ProductoDominio>>() {
                    }.getType());
                    for (int i = 0; i < productosrecuperados.size(); i++) {
                        if (productosrecuperados.get(i).getFotoProductoString() != null) {
                            productosrecuperados.get(i).setFotoProducto(Base64.getDecoder().
                                    decode(productosrecuperados.get(i).getFotoProductoString()));
                        }
                    }
                    configurarTabla();
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println("exception");
            System.out.println(e);
        }
    }

    private void configurarTabla() {
        for (ProductoDominio util : productosrecuperados) {
            ProductoTablaEmpleado productoTablaEmpleado = new ProductoTablaEmpleado();
            productoTablaEmpleado.setIdProducto(util.getIdProducto());
            productoTablaEmpleado.setNombre(util.getNombre());
            productoTablaEmpleado.setDescripcion(util.getDescripcion());
            productoTablaEmpleado.setPrecio(util.getPrecio());
            productoTablaEmpleado.setIdCategoria(util.getIdCategoria());
            productoTablaEmpleado.setCategoria(util.getCategoria());
            productoTablaEmpleado.setEstadoProducto(util.getEstadoProducto());
            productoTablaEmpleado.setFotoProducto(util.getFotoProducto());
            productoTablaEmpleado.setFotoProductoString(util.getFotoProductoString());
            if (util.getFotoProducto() != null) {
                Image image = new Image(new ByteArrayInputStream(util.getFotoProducto()));
                productoTablaEmpleado.setImageProducto(image);
            }
            listaProductoDominios.add(productoTablaEmpleado);
        }
        TableColumn<ProductoTablaEmpleado, String> colCategoria = new TableColumn<>("categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCategoria.setPrefWidth(100);
        TableColumn<ProductoTablaEmpleado, String> colNombre = new TableColumn<>("nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(150);
        TableColumn<ProductoTablaEmpleado, String> colDescripcion = new TableColumn<>("Descripcion");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colDescripcion.setPrefWidth(200);
        TableColumn<ProductoTablaEmpleado, String> colPrecio = new TableColumn<>("precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPrecio.setPrefWidth(50);
        TableColumn<ProductoTablaEmpleado, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("EstadoProducto"));
        colEstado.setPrefWidth(100);
        TableColumn<ProductoTablaEmpleado, Image> colImagen = new TableColumn<>("Imagen");
        colImagen.setCellValueFactory(new PropertyValueFactory<>("imageProducto"));
        colImagen.setPrefWidth(100);
        colImagen.setCellFactory(param -> new TableCell<ProductoTablaEmpleado, Image>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(item);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                }
            }
        });
        ObservableList<ProductoTablaEmpleado> listaProductosObservable = FXCollections.observableArrayList(listaProductoDominios);
        tb_productos.getColumns().addAll(colCategoria, colNombre, colDescripcion, colPrecio, colEstado, colImagen);
        tb_productos.setItems(listaProductosObservable);
    }

    public void btn_registrarProducto(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistrarModificarPlatillo.fxml"));
        try {
            Parent root = loader.load();
            RegistrarModificarPlatilloController registroProductosController = loader.getController();
            ProductoTablaEmpleado vacio = new ProductoTablaEmpleado();
            registroProductosController.cargarCredenciales(credenciales, false, vacio);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tb_productos.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_modificarProducto(ActionEvent actionEvent) {
        if (tb_productos.getSelectionModel().getSelectedIndex() >= 0) {
            abrirVentanaEdicion();
        } else
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Para editar un producto, " +
                    "favor de seleccionar un elemento de la tabla");
    }

    private void abrirVentanaEdicion() {
        ProductoTablaEmpleado seleccion = (ProductoTablaEmpleado) tb_productos.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistrarModificarPlatillo.fxml"));
        try {
            Parent root = loader.load();
            RegistrarModificarPlatilloController registroProductosController = loader.getController();
            registroProductosController.cargarCredenciales(credenciales, true, seleccion);
            registroProductosController.cargarCategorias();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tb_productos.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_estadoProducto(ActionEvent actionEvent) {
        if (tb_productos.getSelectionModel().getSelectedIndex() < 0)
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Para cambiar el estado de un producto, favor de seleccionarlo de la tabla");
        else {
            Alert mensajeCerrarSesion = new Alert(Alert.AlertType.CONFIRMATION);
            mensajeCerrarSesion.setTitle("Modificacion de estado de producto");
            mensajeCerrarSesion.setHeaderText(null);
            var seleccion = (ProductoTablaEmpleado) tb_productos.getSelectionModel().getSelectedItem();
            String cambio = "";
            if (seleccion.getEstadoProducto().equals(EstadoProducto.Estado_Producto_Disponible))
                cambio = EstadoProducto.Estado_Producto_Sin_Stock;
            else
                cambio = EstadoProducto.Estado_Producto_Disponible;
            mensajeCerrarSesion.setContentText("el producto '" + seleccion.getNombre() + "' tiene el estado actual '" + seleccion.getEstadoProducto() + "'. Desea cambiarlo al estado '" + cambio + "'?");
            ButtonType resultadoBoton = mensajeCerrarSesion.showAndWait().orElse(ButtonType.CANCEL);
            if (resultadoBoton == ButtonType.OK) {
                cambiarEstadoProducto(cambio);
            }
        }
    }

    private void cambiarEstadoProducto(String cambio) {
        ProductoTablaEmpleado seleccion = (ProductoTablaEmpleado) tb_productos.getSelectionModel().getSelectedItem();
        seleccion.setEstadoProducto(cambio);
        ProductoDominio productoModificado = new ProductoDominio();
        productoModificado.setIdProducto(seleccion.getIdProducto());
        productoModificado.setNombre(seleccion.getNombre());
        productoModificado.setDescripcion(seleccion.getDescripcion());
        productoModificado.setPrecio(seleccion.getPrecio());
        productoModificado.setIdCategoria(seleccion.getIdCategoria());
        productoModificado.setCategoria(seleccion.getCategoria());
        productoModificado.setEstadoProducto(seleccion.getEstadoProducto());
        productoModificado.setFotoProductoString(seleccion.getFotoProductoString());
        Call<JsonObject> llamadaPost = servicioProducto.modificarProducto("Bearer " + credenciales.getToken(), productoModificado);
        try {
            Response<JsonObject> respuesta = llamadaPost.execute();
            if (respuesta.isSuccessful()) {
                mostrarMensaje(Alert.AlertType.INFORMATION, "Producto modificado",
                        "Producto modificado con exito");
                tb_productos.getColumns().clear();
                cargarProductos();
            } else {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudo modificar el producto: "
                        + respuesta.message() + respuesta.headers());
            }
        } catch (Exception e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "EXCEPCION");
        }
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public void btn_regresar(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuEmpleado.fxml"));
        try {
            Parent root = loader.load();
            MenuEmpleadoController controladorMenuEmpleado = loader.getController();
            controladorMenuEmpleado.cargarCredenciales(credenciales);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tb_productos.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

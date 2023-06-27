package com.example.uveats_escritorio;

import Dominio.CategoriaDominio;
import Dominio.ProductoDominio;
import Dominio.ProductoTabla;
import Dominio.ResenaDominio;
import DominioRespuesta.LoginRespuesta;
import Interfaces.CategoriaInterface;
import Interfaces.ProductoInterface;
import Interfaces.ResenaInterface;
import Utilidades.CodigoOperacion;
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
import java.util.ArrayList;
import java.util.List;

public class ResenaPlatilloController {
    @FXML
    private TableView tb_productos;
    @FXML
    private TextField tf_seleccion;
    @FXML
    private TextArea tf_opinion;
    @FXML
    private ComboBox cb_categorias;
    @FXML
    private TextField tf_campoBusqueda;
    private LoginRespuesta credenciales;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
            addConverterFactory(GsonConverterFactory.create()).build();
    private CategoriaInterface servicioCategoria = retrofit.create(CategoriaInterface.class);
    private ProductoInterface servicioproducto = retrofit.create(ProductoInterface.class);
    private ResenaInterface servicioResena = retrofit.create(ResenaInterface.class);
    private List<CategoriaDominio> categoriasList = new ArrayList<>();
    private List<ProductoDominio> listaProductoDominios = new ArrayList<>();
    public boolean accesoValido = true;
    private int idSeleccion = 0;
    private Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();
    private ObservableList<ProductoTabla> listaProductosObservable;

    public void btn_buscar(ActionEvent actionEvent) {
    }

    public void btn_seleccionar(ActionEvent actionEvent) {
        ProductoTabla seleccion = (ProductoTabla) tb_productos.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarMensaje(Alert.AlertType.ERROR, "fila no seleccionada",
                    "Favor de seleccionar una elemento de la tabla");
        } else {
            String nombreProducto = "";
            for (var util : listaProductoDominios)
                if (util.getIdProducto() == seleccion.getIdProducto()) {
                    nombreProducto = util.getNombre();
                    idSeleccion = util.getIdProducto();
                }
            tf_seleccion.setText(nombreProducto);
            tf_opinion.setDisable(false);
        }
    }

    public void btn_resenar(ActionEvent actionEvent) {
        if (tf_opinion.getText().isBlank()) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se ha seleccionado ni introducido una reseña");
        } else {
            ResenaDominio nuevaResena = new ResenaDominio();
            nuevaResena.setResena(tf_opinion.getText());
            nuevaResena.setIdProducto(idSeleccion);
            nuevaResena.setIdUsuario(credenciales.getUsuario().getIdUsuario());
            RegistrarResena(nuevaResena);
        }
    }

    private void RegistrarResena(ResenaDominio nuevaResena) {
        try {
            Call<JsonObject> llamadaPUT = servicioResena.registrarResena("Bearer " +
                    credenciales.getToken(), nuevaResena);
            Response respuesta = llamadaPUT.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Reseña registrada",
                            "La reseña fue registrada con exito");
                    cerrarVentana();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            Throwable causaOriginal = e.getCause();
            System.out.println("Se produjo un error al realizar la solicitud: " + causaOriginal.getMessage());
        }
    }

    public void btn_regresar(ActionEvent actionEvent) {
        cerrarVentana();
    }

    public void cerrarVentana() {
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
            Stage login = (Stage) tb_productos.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cargarCredenciales(LoginRespuesta credenciales) {
        this.credenciales = credenciales;
    }

    public void cargarcategorias() {
        Call<JsonObject> llamadaGet = servicioCategoria.recuperarCategoria("Bearer " +
                credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = respuesta.body();
                    JsonArray listaCategoriasJson = (JsonArray) bodyRespuesta.get("categoriasRecuperadas");
                    Gson gson = new GsonBuilder().create();
                    categoriasList = gson.fromJson(listaCategoriasJson, new TypeToken<List<CategoriaDominio>>() {
                    }.getType());
                    configurarCategorias();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                    accesoValido = false;
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
                accesoValido = false;
            }
        } catch (Exception e) {
            System.out.println("exception cat");
            System.out.println(e);
        }
    }

    private void configurarCategorias() {
        try {
            System.out.println("confi");
            ObservableList<String> categorias = FXCollections.observableArrayList();
            for (CategoriaDominio util : categoriasList) {
                System.out.println(util.getCategoria());
                categorias.add(util.getCategoria());
            }
            cb_categorias.setItems(categorias);
            FilteredList<ProductoTabla> listaFiltrada = new FilteredList<>(listaProductosObservable);
            tb_productos.setItems(listaFiltrada);
            cb_categorias.valueProperty().addListener((observable, oldValue, newValue) -> {
                listaFiltrada.setPredicate(a -> {
                    if (cb_categorias.getSelectionModel().getSelectedIndex() < 0) {
                        return true;
                    }
                    return a.getCategoria().equals(cb_categorias.getSelectionModel().getSelectedItem());
                });
            });
        } catch (Exception e) {
            System.out.println("ex conf + " + e);
        }

    }

    public void cargarProductos() {
        Call<JsonObject> llamadaGet = servicioproducto.recuperarProductos("Bearer " +
                credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = respuesta.body();
                    JsonArray listaProductosJson = (JsonArray) bodyRespuesta.get("productosRecuperados");
                    Gson gson = new GsonBuilder().create();
                    listaProductoDominios = gson.fromJson(listaProductosJson, new TypeToken<List<ProductoDominio>>() {
                    }.getType());
                    configurarTabla();
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println("exception");
            System.out.println(e);
        }
    }

    public void configurarTabla() {
        List<ProductoTabla> productosTabla = new ArrayList<>();
        for (ProductoDominio util : listaProductoDominios) {
            ProductoTabla productoNuevo = new ProductoTabla();
            productoNuevo.setIdProducto(util.getIdProducto());
            productoNuevo.setDatos(util.getNombre() + "\n" +
                    "Precio: $" + util.getPrecio() + "\n" +
                    "Descripción: " + util.getDescripcion());
            productoNuevo.setCategoria(util.getCategoria());
            productosTabla.add(productoNuevo);
        }

        TableColumn<ProductoTabla, String> colDatos = new TableColumn<>("Informacion");
        colDatos.setCellValueFactory(new PropertyValueFactory<>("datos"));
        colDatos.setPrefWidth(400);
        tb_productos.getColumns().add(colDatos);
        listaProductosObservable = FXCollections.observableArrayList(productosTabla);
        tb_productos.setItems(listaProductosObservable);
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}

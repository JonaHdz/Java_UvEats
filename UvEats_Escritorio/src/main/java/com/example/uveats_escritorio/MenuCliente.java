package com.example.uveats_escritorio;

import Dominio.CategoriaDominio;
import Dominio.ProductoDominio;
import Dominio.ProductoTabla;
import DominioRespuesta.LoginRespuesta;
import Interfaces.CategoriaInterface;
import Interfaces.ProductoInterface;
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

public class MenuCliente {

    @FXML
    private Label lb_Usuario;

    @FXML
    private TableView tb_Productos;
    @FXML
    private ComboBox cb_categorias;

    private LoginRespuesta loginRespuesta;
    List<ProductoDominio> listaProductoDominios = new ArrayList<>();
    List<CategoriaDominio> categoriasList = new ArrayList<>();

    private List<ProductoTabla> productosTabla = new ArrayList<>();
    public boolean accesoValido = true;
    private ObservableList<ProductoTabla> listaProductosObservable;

    public void configurarTabla() {
        productosTabla = new ArrayList<>();
        for (ProductoDominio util : listaProductoDominios) {
            ProductoTabla productoNuevo = new ProductoTabla();
            productoNuevo.setIdProducto(util.getIdProducto());
            productoNuevo.setCategoria(util.getCategoria());
            productoNuevo.setDatos(util.getNombre() + "\n" +
                    "Precio: $" + util.getPrecio() + "\n" +
                    "Descripción: " + util.getDescripcion());
            if (util.getFotoProductoString() != null) {
                byte[] imagenByte = Base64.getDecoder().decode(util.getFotoProductoString());
                productoNuevo.setImageProducto(new Image(new ByteArrayInputStream(imagenByte)));
            }
            productosTabla.add(productoNuevo);
        }

        TableColumn<ProductoTabla, String> colDatos = new TableColumn<>("Informacion");
        colDatos.setCellValueFactory(new PropertyValueFactory<>("datos"));
        colDatos.setPrefWidth(250);
        TableColumn<ProductoTabla, Image> colImagen = new TableColumn<>("Imagen");
        colImagen.setCellValueFactory(new PropertyValueFactory<>("imageProducto"));
        colImagen.setPrefWidth(300);
        colImagen.setCellFactory(param -> new TableCell<ProductoTabla, Image>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(item);
                    imageView.setFitWidth(200);
                    imageView.setFitHeight(100);
                    setGraphic(imageView);
                }
            }
        });
        tb_Productos.getColumns().addAll(colDatos, colImagen);
        listaProductosObservable = FXCollections.observableArrayList(productosTabla);
        tb_Productos.setItems(listaProductosObservable);
        cargarCategorias();
    }

    private void cargarCategorias() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
                addConverterFactory(GsonConverterFactory.create())
                .build();
        CategoriaInterface servicio = retrofit.create(CategoriaInterface.class);
        Call<JsonObject> llamadaGet = servicio.recuperarCategoria("Bearer " + loginRespuesta.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != 403) {
                JsonObject bodyRespuesta = respuesta.body();
                int codigo = bodyRespuesta.get("codigo").getAsInt();
                if (codigo == CodigoOperacion.EXITO) {
                    JsonArray listaCategoriasJson = (JsonArray) bodyRespuesta.get("categoriasRecuperadas");
                    Gson gson = new GsonBuilder().create();
                    categoriasList = gson.fromJson(listaCategoriasJson, new TypeToken<List<CategoriaDominio>>() {
                    }.getType());
                    configurarCategorias();
                } else if (codigo == CodigoOperacion.ERROR_CONEXION) {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
                    accesoValido = false;
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuencta con la autorizacion");
                accesoValido = false;
            }
        } catch (Exception e) {
            System.out.println("exception");
            System.out.println(e);
        }
    }

    private void configurarCategorias() {
        ObservableList<String> categorias = FXCollections.observableArrayList();
        for (CategoriaDominio util : categoriasList) {
            System.out.println(util.getCategoria());
            categorias.add(util.getCategoria());
        }
        cb_categorias.setItems(categorias);
        FilteredList<ProductoTabla> listaFiltrada = new FilteredList<>(listaProductosObservable);
        tb_Productos.setItems(listaFiltrada);
        cb_categorias.valueProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(a -> {
                if (cb_categorias.getSelectionModel().getSelectedIndex() < 0) {
                    return true;
                }
                return a.getCategoria().equals(cb_categorias.getSelectionModel().getSelectedItem());
            });
        });
    }

    public void cargarUsuario(LoginRespuesta loginRespuesta) {
        this.loginRespuesta = loginRespuesta;
        lb_Usuario.setText(this.loginRespuesta.getUsuario().getNombre() + " " +
                this.loginRespuesta.getUsuario().getApellido());
    }


    public void cargarProductos() {
        System.out.println("cargando productos");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
                addConverterFactory(GsonConverterFactory.create())
                .build();
        ProductoInterface servicio = retrofit.create(ProductoInterface.class);
        Call<JsonObject> llamadaGet = servicio.recuperarProductos("Bearer " + loginRespuesta.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                JsonObject bodyRespuesta = respuesta.body();
                int codigo = bodyRespuesta.get("codigo").getAsInt();
                if (codigo == CodigoOperacion.EXITO) {
                    JsonArray listaProductosJson = (JsonArray) bodyRespuesta.get("productosRecuperados");
                    Gson gson = new GsonBuilder().create();
                    listaProductoDominios = gson.fromJson(listaProductosJson, new TypeToken<List<ProductoDominio>>() {
                    }.getType());
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                    accesoValido = false;
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
                accesoValido = false;
            }
        } catch (Exception e) {
            System.out.println("exception");
            System.out.println(e);
        }
    }

    public void btn_ActualizarDatos(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroCliente.fxml"));
        try {
            Parent root = loader.load();
            RegistroCliente controladorRegistroCliente = loader.getController();
            controladorRegistroCliente.CargarEdicion(loginRespuesta, true);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage menu = (Stage) lb_Usuario.getScene().getWindow();
            menu.close();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_CerrarSesion(ActionEvent actionEvent) {
        Cerrarventana();
    }

    public void btn_HistorialCompras(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HistorialCompras.fxml"));
            Parent root = loader.load();
            HistorialComprasController historialComprasController = loader.getController();
            historialComprasController.cargarCredenciales(loginRespuesta);
            if (historialComprasController.accesoValido) {
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage menu = (Stage) lb_Usuario.getScene().getWindow();
                menu.close();
                stage.show();
            } else {
                cargarInicioSesion();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void btn_ReseñarPlatillo(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResenaPlatillo.fxml"));
            Parent root = loader.load();
            ResenaPlatilloController controladorResenasPlatilloController = loader.getController();
            controladorResenasPlatilloController.cargarCredenciales(loginRespuesta);
            controladorResenasPlatilloController.cargarProductos();
            controladorResenasPlatilloController.cargarcategorias();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage menu = (Stage) lb_Usuario.getScene().getWindow();
            menu.close();
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void btn_VerReseñasPlatillo(ActionEvent actionEvent) {
        ProductoTabla seleccion = (ProductoTabla) tb_Productos.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarMensaje(Alert.AlertType.ERROR, "fila no seleccionada",
                    "Favor de seleccionar una elemento de la tabla");
        } else {
            String nombreProducto = "";
            for (var util : listaProductoDominios)
                if (util.getIdProducto() == seleccion.getIdProducto())
                    nombreProducto = util.getNombre();
            abrirReseñas(seleccion.getIdProducto(), nombreProducto);
        }
    }

    private void abrirReseñas(int id, String nombreProducto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListaResenas.fxml"));
            Parent root = loader.load();
            ListaResenasController controladorListaResenasController = loader.getController();
            controladorListaResenasController.cargarCredenciales(loginRespuesta);
            controladorListaResenasController.recuperarReseñas(id, nombreProducto);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage menu = (Stage) lb_Usuario.getScene().getWindow();
            menu.close();
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void btn_Ordenar(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProcesoDeCompra.fxml"));
        try {
            Parent root = loader.load();
            ProcesoDeCompraController controladorCompra = loader.getController();
            controladorCompra.cargarCredenciales(loginRespuesta);
            controladorCompra.cargarProductos();
            controladorCompra.cargarcategorias();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage menu = (Stage) lb_Usuario.getScene().getWindow();
            menu.close();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_Notificaciones(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NotificacionesCliente.fxml"));
        try {
            Parent root = loader.load();
            NotificacionesCliente notificacionesCliente = loader.getController();
            notificacionesCliente.cargarCredenciales(loginRespuesta, loginRespuesta.getUsuario().getIdUsuario());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage stageActual = (Stage) cb_categorias.getScene().getWindow();
            stageActual.close();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Cerrarventana() {
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
                Stage stageActual = (Stage) cb_categorias.getScene().getWindow();
                stageActual.close();
                stage.show();
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

    private void cargarInicioSesion() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("InicioSesion.fxml"));
        try {
            Parent root = loader.load();
            InicioSesionController controlador = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage stageActual = (Stage) tb_Productos.getScene().getWindow();
            stageActual.close();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

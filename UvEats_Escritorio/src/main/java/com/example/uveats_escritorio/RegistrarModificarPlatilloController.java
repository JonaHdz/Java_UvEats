package com.example.uveats_escritorio;

import Dominio.CategoriaDominio;
import Dominio.ProductoDominio;
import Dominio.ProductoTablaEmpleado;
import DominioRespuesta.LoginRespuesta;
import Interfaces.CategoriaInterface;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RegistrarModificarPlatilloController {

    @FXML
    private TextField tf_nombre;
    @FXML
    private TextField tf_Precio;
    @FXML
    private ComboBox cb_categorias;
    @FXML
    private TextArea tf_imagenDefecto;
    @FXML
    private TextArea tf_descripcion;

    @FXML
    private ImageView img_producto;

    private LoginRespuesta credenciales;
    private List<CategoriaDominio> categoriasList = new ArrayList<>();
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).addConverterFactory(GsonConverterFactory.create()).build();
    private CategoriaInterface servicioCategoria = retrofit.create(CategoriaInterface.class);
    private ProductoInterface servicioProducto = retrofit.create(ProductoInterface.class);
    private boolean edicion;
    private Image imagenSeleccionada;
    private ProductoTablaEmpleado productoEdicion;
    private byte[] imagenbyte;

    public void cargarCredenciales(LoginRespuesta credenciales, boolean edicion,
                                   ProductoTablaEmpleado productoEdicion) {
        this.credenciales = credenciales;
        this.edicion = edicion;
        this.productoEdicion = productoEdicion;
        cargarCategorias();
        if (edicion == true)
            cargarEdicion();
    }

    private void cargarEdicion() {
        tf_descripcion.setText(productoEdicion.getDescripcion());
        tf_Precio.setText("" + productoEdicion.getPrecio());
        tf_nombre.setText(productoEdicion.getNombre());
        imagenbyte = productoEdicion.getFotoProducto();
        img_producto.setImage(productoEdicion.getImageProducto());
        tf_imagenDefecto.setVisible(false);
        int contador = 1;
        for (var util : categoriasList) {
            if (util.getCategoria().equals(productoEdicion.getCategoria())) {
                cb_categorias.setValue(util.getCategoria());
                break;
            }
        }
        imagenSeleccionada = productoEdicion.getImageProducto();
    }

    public void cargarCategorias() {
        Call<JsonObject> llamadaGet = servicioCategoria.recuperarCategoria("Bearer " + credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() != CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = respuesta.body();
                    JsonArray listaCategoriasJson = (JsonArray) bodyRespuesta.get("categoriasRecuperadas");
                    Gson gson = new GsonBuilder().create();
                    categoriasList = gson.fromJson(listaCategoriasJson, new TypeToken<List<CategoriaDominio>>() {
                    }.getType());
                    configurarCategorias();
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

    private void configurarCategorias() {
        ObservableList<String> categorias = FXCollections.observableArrayList();
        for (CategoriaDominio util : categoriasList) {
            categorias.add(util.getCategoria());
        }
        cb_categorias.setItems(categorias);
    }

    public void btn_subirFoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar una imagen del sistema");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de imagen", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            imagenSeleccionada = new Image(archivoSeleccionado.toURI().toString());
            img_producto.setImage(imagenSeleccionada);
            tf_imagenDefecto.setVisible(false);
            try {
                imagenbyte = Files.readAllBytes(archivoSeleccionado.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btn_finalizarRegistro(ActionEvent actionEvent) {
        if (cb_categorias.getSelectionModel().getSelectedIndex() >= 0)
            if (!tf_nombre.getText().isBlank())
                if (!tf_descripcion.getText().isBlank())
                    if (!tf_Precio.getText().isBlank() && ValidacionFormatoPresio())
                        if (imagenSeleccionada != null)
                            if (edicion == true)
                                guardarEdicion();
                            else
                                registrarProducto();
                        else
                            mostrarMensaje(Alert.AlertType.ERROR, "Campo obligatorio", "Para registrar un producto se nececita una imagen");
                    else
                        mostrarMensaje(Alert.AlertType.ERROR, "Campo obligatorio", "Para registrar un producto se nececita ingresar un precio (valores numericos positivos)");
                else
                    mostrarMensaje(Alert.AlertType.ERROR, "Campo obligatorio", "Para registrar un producto se nececita ingresar una descripción");
            else
                mostrarMensaje(Alert.AlertType.ERROR, "Campo obligatorio", "Para registrar un producto se nececita ingresar un nombre");
        else
            mostrarMensaje(Alert.AlertType.ERROR, "Campo obligatorio", "para registrar un producto se necesita seleccionar una categoria");
    }

    private void guardarEdicion() {
        ProductoDominio nuevoProducto = new ProductoDominio();
        nuevoProducto.setNombre(tf_nombre.getText());
        nuevoProducto.setEstadoProducto(EstadoProducto.Estado_Producto_Disponible);
        nuevoProducto.setDescripcion(tf_descripcion.getText());
        nuevoProducto.setPrecio(Double.parseDouble(tf_Precio.getText()));
        nuevoProducto.setIdProducto(productoEdicion.getIdProducto());
        try {
            String imagenBase64 = Base64.getEncoder().encodeToString(imagenbyte);
            nuevoProducto.setFotoProductoString(imagenBase64);
        } catch (Exception ee) {
            System.out.println("Base 64: " + ee.getMessage());
        }
        String categoriaSeleccionada = cb_categorias.getSelectionModel().getSelectedItem().toString();
        CategoriaDominio categoria = null;
        for (CategoriaDominio cat : categoriasList) {
            if (cat.getCategoria().equals(categoriaSeleccionada)) {
                categoria = cat;
                break;
            }
        }
        nuevoProducto.setIdCategoria(categoria.getIdCategoria());
        byte[] foto = imageToBytes(imagenSeleccionada);
        if (foto != null)
            nuevoProducto.setFotoProducto(null);
        Call<JsonObject> llamadaPost = servicioProducto.modificarProducto("Bearer " + credenciales.getToken(), nuevoProducto);
        try {
            Response<JsonObject> respuesta = llamadaPost.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() != CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Producto modificado", "Producto modificado con exito");
                    cargarVentanaGestionProductos();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");

            }
        } catch (Exception e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "EXCEPCION");
        }
    }

    private void registrarProducto() {
        ProductoDominio nuevoProducto = new ProductoDominio();
        nuevoProducto.setNombre(tf_nombre.getText());
        nuevoProducto.setEstadoProducto(EstadoProducto.Estado_Producto_Disponible);
        nuevoProducto.setDescripcion(tf_descripcion.getText());
        try {
            String imagenBase64 = Base64.getEncoder().encodeToString(imagenbyte);
            nuevoProducto.setFotoProductoString(imagenBase64);
        } catch (Exception ee) {
            System.out.println("Base 64: " + ee.getMessage());
        }
        nuevoProducto.setPrecio(Double.parseDouble(tf_Precio.getText()));
        String categoriaSeleccionada = cb_categorias.getSelectionModel().getSelectedItem().toString();
        CategoriaDominio categoria = null;
        for (CategoriaDominio cat : categoriasList) {
            if (cat.getCategoria().equals(categoriaSeleccionada)) {
                categoria = cat;
                break;
            }
        }
        nuevoProducto.setIdCategoria(categoria.getIdCategoria());
        byte[] foto = imageToBytes(imagenSeleccionada);
        if (foto != null)
            nuevoProducto.setFotoProducto(null);
        Call<JsonObject> llamadaPost = servicioProducto.registrarProducto("Bearer " +
                credenciales.getToken(), nuevoProducto);
        try {
            Response<JsonObject> respuesta = llamadaPost.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Producto registrado",
                            "Producto registrado cone xito");
                    cargarVentanaGestionProductos();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println("OCURRIO UN PROBLEMA: " + e);
        }
    }

    private byte[] imageToBytes(Image image) {
        try {
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            byte[] pixelData = new byte[width * height * 4];
            PixelReader pixelReader = image.getPixelReader();
            pixelReader.getPixels(0, 0, width, height, javafx.scene.image.PixelFormat.getByteBgraInstance(), pixelData, 0, width * 4);
            return pixelData;
        } catch (Exception e) {
            System.out.println("Catch");
        }
        return null;
    }

    private boolean ValidacionFormatoPresio() {
        boolean formatoCorrecto = false;
        try {
            double validacionPrecio = Double.parseDouble(tf_Precio.getText());
            if (validacionPrecio > 0)
                formatoCorrecto = true;
            else
                formatoCorrecto = false;
        } catch (Exception e) {
            formatoCorrecto = false;
        }
        return formatoCorrecto;
    }

    public void btn_cancelar(ActionEvent actionEvent) {
        Alert mensajeCerrarSesion = new Alert(Alert.AlertType.CONFIRMATION);
        mensajeCerrarSesion.setTitle("Cerrar sesión");
        mensajeCerrarSesion.setHeaderText(null);
        mensajeCerrarSesion.setContentText("Está seguro de cerrar la venta?cualquier cambio o " +
                "informacion ingresada se perderá");
        ButtonType resultadoBoton = mensajeCerrarSesion.showAndWait().orElse(ButtonType.CANCEL);
        if (resultadoBoton == ButtonType.OK) {
            cargarVentanaGestionProductos();
        }
    }

    private void cargarVentanaGestionProductos() {
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
            Stage menu = (Stage) tf_descripcion.getScene().getWindow();
            menu.close();
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}

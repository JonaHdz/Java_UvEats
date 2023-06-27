package com.example.uveats_escritorio;

import Dominio.*;
import DominioRespuesta.LoginRespuesta;
import Interfaces.CategoriaInterface;
import Interfaces.PedidoInterface;
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
import javafx.util.converter.IntegerStringConverter;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class ProcesoDeCompraController {
    @FXML
    private TableView tb_productos;
    @FXML
    private ComboBox cb_categoria;
    @FXML
    private TextField tf_cantidad;
    @FXML
    private TableView tb_carrito;
    private LoginRespuesta credenciales;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).addConverterFactory(GsonConverterFactory.create()).build();
    private CategoriaInterface servicioCategoria = retrofit.create(CategoriaInterface.class);
    private PedidoInterface servicioPedido = retrofit.create(PedidoInterface.class);
    private List<ProductoDominio> listaProductoDominios = new ArrayList<>();
    private List<CategoriaDominio> categoriasList = new ArrayList<>();
    private List<CarritoTabla> carritoList = new ArrayList<>();
    private ObservableList<CarritoTabla> carritoListObservable = FXCollections.observableArrayList(carritoList);
    private ProductoInterface servicioProducto = retrofit.create(ProductoInterface.class);
    public void cargarCredenciales(LoginRespuesta credenciales) {
        this.credenciales = credenciales;
    }
    private ObservableList<ProductoTabla> listaProductosObservable;

    public void cargarProductos() {
        Call<JsonObject> llamadaGet = servicioProducto.recuperarProductos("Bearer " +
                credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if(respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = respuesta.body();
                    JsonArray listaProductosJson = (JsonArray) bodyRespuesta.get("productosRecuperados");
                    Gson gson = new GsonBuilder().create();
                    listaProductoDominios = gson.fromJson(listaProductosJson, new TypeToken<List<ProductoDominio>>() {
                    }.getType());
                    configurarTabla();
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR,"ERROR","Error de conexión");
            }else{
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR,"ERROR","No se cuenta con el acceso");
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
            productoNuevo.setCategoria(util.getCategoria());
            productoNuevo.setDatos(util.getNombre() + "\n" +
                    "Precio: $" + util.getPrecio() + "\n" +
                    "Descripción: " + util.getDescripcion());
            if(util.getFotoProductoString()!= null){
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

        tb_productos.getColumns().addAll(colDatos,colImagen);
        listaProductosObservable = FXCollections.observableArrayList(productosTabla);
        tb_productos.setItems(listaProductosObservable);

    }

    public void cargarcategorias() {
        Call<JsonObject> llamadaGet = servicioCategoria.recuperarCategoria("Bearer " +
                credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if(respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = respuesta.body();
                    JsonArray listaCategoriasJson = (JsonArray) bodyRespuesta.get("categoriasRecuperadas");
                    Gson gson = new GsonBuilder().create();
                    categoriasList = gson.fromJson(listaCategoriasJson, new TypeToken<List<CategoriaDominio>>() {
                    }.getType());
                    configurarCategorias();
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR,"ERROR","Error de conexion");
            }else{
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR,"ERROR","No se cuenta con el acceso");
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
        cb_categoria.setItems(categorias);
        FilteredList<ProductoTabla> listaFiltrada = new FilteredList<>(listaProductosObservable);
        tb_productos.setItems(listaFiltrada);
        cb_categoria.valueProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(a -> {
                if (cb_categoria.getSelectionModel().getSelectedIndex()<0) {
                    return true;
                }
                return a.getCategoria().equals(cb_categoria.getSelectionModel().getSelectedItem());
            });
        });
        configurarCampoCantidad();
        COnfigurarTablaCarrito();
    }

    private void COnfigurarTablaCarrito() {
        TableColumn<CarritoTabla, String> colCompra = new TableColumn<>("Compra");
        colCompra.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCompra.setPrefWidth(200);
        TableColumn<CarritoTabla, String> colCantidad = new TableColumn<>("cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setPrefWidth(200);
        TableColumn<CarritoTabla, String> colSubtotal= new TableColumn<>("subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotal.setPrefWidth(200);
        tb_carrito.getColumns().addAll(colCompra,colCantidad,colSubtotal);
        tb_carrito.setItems(carritoListObservable);
    }

    public void btn_eliminar(ActionEvent actionEvent) {
        CarritoTabla seleccion = (CarritoTabla) tb_carrito.getSelectionModel().getSelectedItem();
        if(seleccion!=null)
            carritoListObservable.remove(seleccion);
            else
                mostrarMensaje(Alert.AlertType.ERROR,"elemento no seleccionado",
                        "para eliminar un producto de su carrito, favor de seleccionarlo del 'carrito'");
    }

    public void btn_realizarPedido(ActionEvent actionEvent) {
        PedidoDominio nuevoPedido  = new PedidoDominio();
        nuevoPedido.setIdUsuario(credenciales.getUsuario().getIdUsuario());
        float total = 0;
        for(var util : carritoListObservable){
            total = (float) (total + util.getSubtotal());
        }
        nuevoPedido.setTotal(total);
        List<ProductoPedidoDominio> productosPedidoList = new ArrayList<>();
        for(var util : carritoListObservable){
            ProductoPedidoDominio nuevoProductoPedido = new ProductoPedidoDominio();
            nuevoProductoPedido.setIdProducto(util.getIdProducto());
            nuevoProductoPedido.setCantidad(util.getCantidad());
            nuevoProductoPedido.setSubtotal(util.getSubtotal());
            nuevoProductoPedido.setNombreProducto(util.getNombreProducto());
            productosPedidoList.add(nuevoProductoPedido);
        }
        nuevoPedido.setProductosPedido(productosPedidoList);
        Call<JsonObject> llamadaPut = servicioPedido.realizarPedido("Bearer " +
                credenciales.getToken(),nuevoPedido);
        try {
            Response<JsonObject> respuesta = llamadaPut.execute();
            if(respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    mostrarMensaje(Alert.AlertType.INFORMATION,"Pedido realizado",
                            "Tu pedido fue registrado, podrás consultar su estado en la seccion conversacion del menu");
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR,"ERROR","Error de conexion");
            }else{
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR,"ERROR","No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println("exception");
            System.out.println(e);
        }
    }

    public void btn_cancelarPedido(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText("¿Desea cancelar?");
        ButtonType buttonAceptar = new ButtonType("Aceptar");
        ButtonType buttonCancelar = new ButtonType("Cancelar");
        alert.getButtonTypes().setAll(buttonAceptar, buttonCancelar);
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonAceptar) {
                cerrarventana();
            } else if (buttonType == buttonCancelar) {

            }
        });
    }

    private void cerrarventana() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuCliente.fxml"));
        try {
            Parent root = loader.load();
            MenuCliente controladorMenuCliente = loader.getController();
            controladorMenuCliente.cargarUsuario(credenciales);
            controladorMenuCliente.cargarProductos();
            controladorMenuCliente.configurarTabla();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) cb_categoria.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_agregar(ActionEvent actionEvent) {
        ProductoTabla seleccion = (ProductoTabla) tb_productos.getSelectionModel().getSelectedItem();
        if (seleccion != null)
            if (tf_cantidad.getText().isBlank())
                mostrarMensaje(Alert.AlertType.ERROR, "cantidad no introducida",
                        "Favor de introducir una cantidad del producto deseado");
            else
                agregarProductoCarrito(seleccion);
        else
            mostrarMensaje(Alert.AlertType.ERROR, "elemento no seleccionado",
                    "para agregar a carrito, favor de seleccionar un producto");
    }

    private void agregarProductoCarrito(ProductoTabla seleccion) {
        Optional<ProductoDominio> producto = listaProductoDominios.stream()
                .filter(util -> util.getIdProducto() == seleccion.getIdProducto())
                .findFirst();
        if(producto.isPresent()){
            CarritoTabla nuevoProducto = new CarritoTabla();
            nuevoProducto.setNombreProducto(producto.get().getNombre());
            nuevoProducto.setIdProducto(producto.get().getIdProducto());
            nuevoProducto.setCantidad(Integer.parseInt(tf_cantidad.getText()));
            nuevoProducto.setSubtotal((float) (Integer.parseInt(tf_cantidad.getText()) * producto.get().getPrecio()));
            carritoListObservable.add(nuevoProducto);
        }
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public void configurarCampoCantidad() {
        try {
            int maxLongitud = 2;
            tf_cantidad.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > maxLongitud) {
                    tf_cantidad.setText(oldValue);
                }
            });
            TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), null, c -> {
                if (c.getControlNewText().matches("\\d*")) {
                    return c;
                }
                return null;
            });
            tf_cantidad.setTextFormatter(textFormatter);
        } catch (IllegalArgumentException e) {
        }
    }
}

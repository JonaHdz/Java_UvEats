package com.example.uveats_escritorio;

import Dominio.ResenaRecibidaDominio;
import DominioRespuesta.LoginRespuesta;
import DominioRespuesta.ResenasRespuesta;
import Interfaces.ResenaInterface;
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
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

public class ListaResenasController {

    @FXML
    private TableView tb_resenas;
    @FXML
    private TextField tf_productoNombre;
    private LoginRespuesta credenciales;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
            addConverterFactory(GsonConverterFactory.create()).build();
    private ResenaInterface servicioResena = retrofit.create(ResenaInterface.class);
    private List<ResenaRecibidaDominio> resenas = new ArrayList<>();
    private String productoNombre = "";

    public void cargarCredenciales(LoginRespuesta loginRespuesta) {
        credenciales = loginRespuesta;
    }

    public void recuperarReseñas(int idProducto, String nombreProducto) {
        this.productoNombre = nombreProducto;
        Call<JsonObject> llamadaGet = servicioResena.recuperarResenasPorId("Bearer " +
                credenciales.getToken(), idProducto);
        try {
            Response respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    System.out.println(respuesta.body());
                    JsonObject bodyRespuesta = (JsonObject) respuesta.body();
                    int codigo = bodyRespuesta.get("codigo").getAsInt();
                    JsonArray listaJson = (JsonArray) bodyRespuesta.get("resenasRecuperas");
                    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                            new LocalDateTimeAdapter()).create();
                    ResenasRespuesta resenasRespuesta = new ResenasRespuesta();
                    resenasRespuesta.setCodigo(codigo);
                    try {
                        resenas = gson.fromJson(listaJson, new TypeToken<List<ResenaRecibidaDominio>>() {
                        }.getType());
                        cargarTablaResenas();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void cargarTablaResenas() {
        TableColumn<ResenaRecibidaDominio, String> colResena = new TableColumn<>("resena");
        colResena.setCellValueFactory(new PropertyValueFactory<>("resena"));
        colResena.setPrefWidth(700);
        TableColumn<ResenaRecibidaDominio, String> colFecha = new TableColumn<>("fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        tb_resenas.getColumns().addAll(colResena, colFecha);
        ObservableList<ResenaRecibidaDominio> listaResenasObservable = FXCollections.observableArrayList(resenas);
        tb_resenas.setItems(listaResenasObservable);
        tf_productoNombre.setText(productoNombre);
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
            Stage login = (Stage) tb_resenas.getScene().getWindow();
            login.close();
            stage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

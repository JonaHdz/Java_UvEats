package com.example.uveats_escritorio;

import Dominio.CategoriaDominio;
import DominioRespuesta.LoginRespuesta;
import Interfaces.CategoriaInterface;
import Interfaces.ComunicacionVentanas;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class GestionCategoriasController implements ComunicacionVentanas {
    @FXML
    private TableView tb_categorias;
    private LoginRespuesta credenciales;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).addConverterFactory(GsonConverterFactory.create()).build();
    private CategoriaInterface servicioCategoria = retrofit.create(CategoriaInterface.class);
    private List<CategoriaDominio> categoriasList = new ArrayList<>();
    private ObservableList<CategoriaDominio> categoriasObservable;

    public void cargarCredenciales(LoginRespuesta loginRespuesta) {
        credenciales = loginRespuesta;
        cargarCategorias();
    }

    public void cargarCategorias() {
        Call<JsonObject> llamadaGet = servicioCategoria.recuperarCategoria("Bearer " +
                credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.ACCESO_DENEGADO && respuesta.code() !=
                    CodigoOperacion.SIN_PERMISO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = respuesta.body();
                    JsonArray listaCategoriasJson = (JsonArray) bodyRespuesta.get("categoriasRecuperadas");
                    Gson gson = new GsonBuilder().create();
                    categoriasList = gson.fromJson(listaCategoriasJson, new TypeToken<List<CategoriaDominio>>() {
                    }.getType());
                    configurarCategorias();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println("exception");
            System.out.println(e);
        }
    }

    private void configurarCategorias() {
        TableColumn<CategoriaDominio, String> colId = new TableColumn<>("Identificador");
        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colId.setPrefWidth(200);
        colId.setStyle("-fx-font-size: 20px;");
        TableColumn<CategoriaDominio, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCategoria.setPrefWidth(400);
        colCategoria.setStyle("-fx-font-size: 20px;");
        tb_categorias.getColumns().addAll(colId, colCategoria);
        categoriasObservable = FXCollections.observableArrayList(categoriasList);
        tb_categorias.setItems(categoriasObservable);
    }

    public void btn_agregarCategoria(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroModificacionCategoria.fxml"));
        try {
            Parent root = loader.load();

            RegistroModificacionCategoriaController registroModificacionCategoriaController = loader.getController();
            registroModificacionCategoriaController.cargarCredenciales(credenciales, false, null, this);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_modificarCategoria(ActionEvent actionEvent) {
        if ((CategoriaDominio) tb_categorias.getSelectionModel().getSelectedItem() == null)
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Para editar una categoria, " +
                    "favor de seleccionar una categoria de la tabla");
        else {
            CategoriaDominio seleccion = (CategoriaDominio) tb_categorias.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroModificacionCategoria.fxml"));
            try {
                Parent root = loader.load();

                RegistroModificacionCategoriaController registroModificacionCategoriaController = loader.getController();
                registroModificacionCategoriaController.cargarCredenciales(credenciales, true, seleccion, this);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
            Stage login = (Stage) tb_categorias.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void Actualizarinfousuario() {
        cargarCategorias();
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}

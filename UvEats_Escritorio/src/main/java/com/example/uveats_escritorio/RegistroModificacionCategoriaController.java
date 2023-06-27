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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class RegistroModificacionCategoriaController {
    @FXML
    private TextField tf_categoria;
    private LoginRespuesta credenciales;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
            addConverterFactory(GsonConverterFactory.create()).build();
    private CategoriaInterface servicioCategoria = retrofit.create(CategoriaInterface.class);
    private boolean edicion;
    private CategoriaDominio categoriaEdicion;
    private ComunicacionVentanas comunicacionVentanas;
    private List<CategoriaDominio> categoriasList = new ArrayList<>();

    public void cargarCredenciales(LoginRespuesta loginRespuesta, boolean edicion,
                                   CategoriaDominio categoriaEdicion, ComunicacionVentanas comunicacionVentanas) {
        credenciales = loginRespuesta;
        this.comunicacionVentanas = comunicacionVentanas;
        this.categoriaEdicion = categoriaEdicion;
        this.edicion = edicion;
        if (edicion)
            tf_categoria.setText(categoriaEdicion.getCategoria());
    }

    public void btn_regresar(ActionEvent actionEvent) {
        Stage stage = (Stage) tf_categoria.getScene().getWindow();
        stage.close();
    }

    public void btn_guardar(ActionEvent actionEvent) {
        if (!tf_categoria.getText().isBlank())
            if (categoriaUnica()) {
                if (!edicion) {
                    CategoriaDominio nuevaCategoria = new CategoriaDominio(0, tf_categoria.getText());
                    guardarCategoria(nuevaCategoria);
                } else {
                    CategoriaDominio nuevaCategoria = new CategoriaDominio(categoriaEdicion.getIdCategoria(),
                            tf_categoria.getText());
                    EditarCategoria(nuevaCategoria);
                }
            } else
                mostrarMensaje(Alert.AlertType.ERROR, "Categoria invalida", "Ya existe una categoria con el mismo nombre");
        else
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "El campo se encuntra vacio");
    }

    private void EditarCategoria(CategoriaDominio nuevaCategoria) {
        Call<JsonObject> llamadaGet = servicioCategoria.modificarCategoria("Bearer " + credenciales.getToken(), nuevaCategoria);
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() != CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    cerrarVentana();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "categoria modificada", "Categoria Modifiada");
                    comunicacionVentanas.Actualizarinfousuario();
                    cerrarVentana();
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

    private void guardarCategoria(CategoriaDominio nuevaCategoria) {
        Call<JsonObject> llamadaGet = servicioCategoria.registrarCategoria("Bearer " + credenciales.getToken(), nuevaCategoria);
        try {
            Response<JsonObject> respuesta = llamadaGet.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() != CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "categoria registrada", "Categoria Registrada");
                    cerrarVentana();
                    comunicacionVentanas.Actualizarinfousuario();
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

    private void cerrarVentana() {
        Stage stage = (Stage) tf_categoria.getScene().getWindow();
        stage.close();
    }

    private boolean categoriaUnica() {
        boolean categoriaValida = true;
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
                    for (var util : categoriasList)
                        if (tf_categoria.getText().toUpperCase().equals(util.getCategoria().toUpperCase()))
                            categoriaValida = false;
                } else
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            System.out.println("exception");
            System.out.println(e);
        }
        return categoriaValida;
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}

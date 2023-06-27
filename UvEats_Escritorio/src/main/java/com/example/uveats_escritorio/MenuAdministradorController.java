package com.example.uveats_escritorio;

import Dominio.EmpleadoDominio;
import Dominio.EmpleadoTabla;
import DominioRespuesta.LoginRespuesta;
import Interfaces.EmpleadoInterface;
import Utilidades.CodigoOperacion;
import Utilidades.Mensaje;
import Utilidades.TipoUsuario;
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

public class MenuAdministradorController {

    @FXML
    private TextField tf_busqueda;
    @FXML
    private TableView tb_empleados;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
            addConverterFactory(GsonConverterFactory.create()).build();
    private EmpleadoInterface servicioEmpleado = retrofit.create(EmpleadoInterface.class);
    private LoginRespuesta credenciales;
    private List<EmpleadoDominio> empleadosList = new ArrayList<>();
    public boolean accesoValido = true;
    private List<EmpleadoTabla> empeladosTablaList = new ArrayList<>();
    private ObservableList<EmpleadoTabla> empeladosObservable;


    public void cargarCredenciales(LoginRespuesta credenciales) {
        this.credenciales = credenciales;
        cargarEmpleados();
    }

    private void cargarEmpleados() {
        empleadosList.clear();
        Call<JsonObject> llamadaRecuperarEmpleados = servicioEmpleado.recuperarEmpleados("Bearer " +
                credenciales.getToken());
        try {
            Response<JsonObject> respuesta = llamadaRecuperarEmpleados.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject body = respuesta.body();
                    JsonArray listaEmpleadosJson = (JsonArray) body.get("lista");
                    Gson gson = new GsonBuilder().create();
                    empleadosList = gson.fromJson(listaEmpleadosJson, new TypeToken<List<EmpleadoDominio>>() {
                    }.getType());
                    for (int i = 0; i < empleadosList.size(); i++) {
                        if (empleadosList.get(i).getFoto() != null) {
                            empleadosList.get(i).setFotoBytes(Base64.getDecoder().decode(empleadosList.get(i).
                                    getFoto()));
                        }
                    }
                    configurarTabla();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                    accesoValido = false;
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
                accesoValido = false;
            }
        } catch (Exception e) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "CATCH", e.getMessage());
        }
    }

    private void configurarTabla() {
        empeladosTablaList.clear();
        tb_empleados.getColumns().clear();
        for (var util : empleadosList) {
            EmpleadoTabla empleadoTabla = new EmpleadoTabla();
            empleadoTabla.setIdUsuario(util.getIdUsuario());
            empleadoTabla.setNombre(util.getNombre());
            empleadoTabla.setApellido(util.getApellido());
            empleadoTabla.setContrasena(util.getContrasena());
            empleadoTabla.setCorreo(util.getCorreo());
            empleadoTabla.setTelefono(util.getTelefono());
            empleadoTabla.setTipo(util.getTipo());
            empleadoTabla.setFoto(util.getFoto());
            empleadoTabla.setFotoBytes(util.getFotoBytes());
            if (util.getFoto() != null) {
                Image image = new Image(new ByteArrayInputStream(util.getFotoBytes()));
                empleadoTabla.setImagen(image);
            }
            empeladosTablaList.add(empleadoTabla);
        }
        try {
            TableColumn<EmpleadoTabla, String> colCorreo = new TableColumn<>("Correo");
            colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
            colCorreo.setPrefWidth(200);
            TableColumn<EmpleadoTabla, String> colNombre = new TableColumn<>("Nombre");
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colNombre.setPrefWidth(100);
            TableColumn<EmpleadoTabla, String> colApellido = new TableColumn<>("Apellido(s)");
            colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
            colApellido.setPrefWidth(150);
            TableColumn<EmpleadoTabla, String> colTelefono = new TableColumn<>("Telefono");
            colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            colTelefono.setPrefWidth(150);
            TableColumn<EmpleadoTabla, String> colTipo = new TableColumn<>("Cargo");
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            colTipo.setPrefWidth(100);
            TableColumn<EmpleadoTabla, Image> colImagen = new TableColumn<>("Imagen");
            colImagen.setCellValueFactory(new PropertyValueFactory<>("imagen"));
            colImagen.setPrefWidth(100);
            colImagen.setCellFactory(param -> new TableCell<EmpleadoTabla, Image>() {
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
            tb_empleados.getColumns().addAll(colTipo, colCorreo, colNombre, colApellido, colTelefono, colImagen);
            empeladosObservable = FXCollections.observableArrayList(empeladosTablaList);
            tb_empleados.setItems(empeladosObservable);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void btn_buscar(ActionEvent actionEvent) {
        FilteredList<EmpleadoTabla> listaFiltrada = new FilteredList<>(empeladosObservable);
        tb_empleados.setItems(listaFiltrada);
        listaFiltrada.setPredicate(a -> {
            if (tf_busqueda.getText().isBlank()) {
                return true;
            }
            return a.getNombre().toLowerCase().contains(tf_busqueda.getText().toLowerCase()) ||
                    a.getApellido().toLowerCase().contains(tf_busqueda.getText().toLowerCase()) ||
                    a.getTelefono().toLowerCase().contains(tf_busqueda.getText().toLowerCase());
        });
    }

    public void btn_elminarEmpleado(ActionEvent actionEvent) {
        if (tb_empleados.getSelectionModel().getSelectedIndex() >= 0) {
            EmpleadoTabla seleccion = (EmpleadoTabla) tb_empleados.getSelectionModel().getSelectedItem();
            if (seleccion.getTipo().equals(TipoUsuario.TIPO_ADMIN)) {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error", "El administrador no puede ser eliminado de sistema");
            } else {
                Alert mensajeCerrarSesion = new Alert(Alert.AlertType.CONFIRMATION);
                mensajeCerrarSesion.setTitle("Eliminar empleado");
                mensajeCerrarSesion.setHeaderText(null);
                mensajeCerrarSesion.setContentText("Está seguro de eliminar al empleado " + seleccion.getNombre() + " " +seleccion.getApellido() + " de la lista de empleados?"  );
                ButtonType resultadoBoton = mensajeCerrarSesion.showAndWait().orElse(ButtonType.CANCEL);
                if (resultadoBoton == ButtonType.OK) {
                    Call<JsonObject> llamadaEliminar = servicioEmpleado.eliminarEmpleado("Bearer " + credenciales.getToken(), seleccion.getIdUsuario());
                    try {
                        Response<JsonObject> resultado = llamadaEliminar.execute();
                        if (resultado.code() != CodigoOperacion.SIN_PERMISO && resultado.code() != CodigoOperacion.ACCESO_DENEGADO) {
                            if (resultado.isSuccessful()) {
                                Mensaje.mostrarMensaje(Alert.AlertType.INFORMATION, "Empleado eliminado", "Empleado elimnado de sistema");
                                cargarEmpleados();
                            } else {
                                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexion");
                            }
                        } else {
                            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
                        }
                    } catch (Exception e) {
                        Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "CATCH", e.getMessage());
                    }
                }
            }
        } else
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error", "Para eliminar un empleado favor de seleccionarlo de la tabla");
    }

    public void btn_editarEmpleado(ActionEvent actionEvent) {
        if (tb_empleados.getSelectionModel().getSelectedIndex() >= 0) {
            EmpleadoTabla seleccion = (EmpleadoTabla) tb_empleados.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroModificacionEmpleado.fxml"));
            try {
                Parent root = loader.load();
                RegistroModificacionEmpleadoController registroModificacionEmpleadoController = loader.getController();
                registroModificacionEmpleadoController.cargarCredenciales(credenciales, true, seleccion);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                Stage stageActual = (Stage) tf_busqueda.getScene().getWindow();
                stageActual.close();
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error", "Para editar un empleado " +
                    "favor de seleccionarlo de la tabla");
    }

    public void btn_AgregarEmpleado(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroModificacionEmpleado.fxml"));
        try {
            Parent root = loader.load();
            RegistroModificacionEmpleadoController registroModificacionEmpleadoController = loader.getController();
            registroModificacionEmpleadoController.cargarCredenciales(credenciales, false, null);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage stageActual = (Stage) tf_busqueda.getScene().getWindow();
            stageActual.close();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                Stage stageActual = (Stage) tf_busqueda.getScene().getWindow();
                stageActual.close();
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

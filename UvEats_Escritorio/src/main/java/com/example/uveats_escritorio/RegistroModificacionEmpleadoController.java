package com.example.uveats_escritorio;

import Dominio.EmpleadoDominio;
import Dominio.EmpleadoTabla;
import DominioRespuesta.LoginRespuesta;
import Interfaces.EmpleadoInterface;
import Interfaces.UsuarioInterface;
import Utilidades.CodigoOperacion;
import Utilidades.Mensaje;
import Utilidades.MetodoCifrado;
import Utilidades.UrlPuertos;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.Base64;

public class RegistroModificacionEmpleadoController {
    @FXML
    private TextField tf_telefono;
    @FXML
    private ImageView img_foto;
    @FXML
    private TextField tf_nombre;
    @FXML
    private TextField tf_apellido;
    @FXML
    private TextField tf_correo;
    @FXML
    private PasswordField tf_contrasena;
    private LoginRespuesta credenciales;
    private boolean edicion;
    private EmpleadoTabla usuarioEdicion;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).
            addConverterFactory(GsonConverterFactory.create()).build();
    private UsuarioInterface servicioCorreo = retrofit.create(UsuarioInterface.class);

    private EmpleadoInterface servicioEmpleado = retrofit.create(EmpleadoInterface.class);
    private Image imagenSeleccionada;
    private byte[] imagenbyte;

    public void cargarCredenciales(LoginRespuesta credenciales, boolean edicion, EmpleadoTabla usuarioEdicion) {
        this.credenciales = credenciales;
        this.usuarioEdicion = usuarioEdicion;
        this.edicion = edicion;
        if (edicion) {
            cargarDatosUsuario();
        }
    }

    private void cargarDatosUsuario() {
        tf_nombre.setText(usuarioEdicion.getNombre());
        tf_apellido.setText(usuarioEdicion.getApellido());
        tf_correo.setText(usuarioEdicion.getCorreo());
        tf_contrasena.setText(MetodoCifrado.desencriptarCadena(usuarioEdicion.getContrasena()));
        tf_telefono.setText(usuarioEdicion.getTelefono());
        img_foto.setImage(usuarioEdicion.getImagen());
        tf_correo.setDisable(true);
    }

    public void btn_regresar(ActionEvent actionEvent) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuAdministrador.fxml"));
        try {
            Parent root = loader.load();
            MenuAdministradorController menuAdministradorController = loader.getController();
            menuAdministradorController.cargarCredenciales(credenciales);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            Stage login = (Stage) tf_apellido.getScene().getWindow();
            login.close();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btn_guardar(ActionEvent actionEvent) {
        if (tf_nombre.getText().isBlank() || tf_apellido.getText().isBlank() || tf_correo.getText().isBlank()
                || tf_contrasena.getText().isBlank()) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error",
                    "los campos * son obligatorios. Favor de verificar");
        } else {
            if (telefonoValido()) {
                if (!edicion) {
                    if (tf_correo.getText().contains("@hotmail.com") || tf_correo.getText().
                            contains("@estudiantes.uv.mx")) {
                        if (correoUnico()) {
                            if (imagenSeleccionada != null) {
                                registrarEmpleado();
                            } else {
                                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error",
                                        "Se requiere un foto del empleado");
                            }
                        } else {
                            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error",
                                    "El correo introducido ya se encuentra registrado en otra cuenta. Favor de verificar");
                        }
                    } else {
                        Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error",
                                "Dominio de correo invalido. Favor de verificar");
                    }
                } else {
                    editarEmpleado();
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error",
                        "Número telefonico invalido.Favor de verificar");
            }
        }
    }

    private void editarEmpleado() {
        EmpleadoDominio nuevoEmpledo = new EmpleadoDominio();
        nuevoEmpledo.setNombre(tf_nombre.getText());
        nuevoEmpledo.setApellido(tf_apellido.getText());
        nuevoEmpledo.setContrasena(tf_contrasena.getText());
        nuevoEmpledo.setTelefono(tf_telefono.getText());
        nuevoEmpledo.setTipo(usuarioEdicion.getTipo());
        nuevoEmpledo.setCorreo(tf_correo.getText());
        nuevoEmpledo.setIdUsuario(usuarioEdicion.getIdUsuario());
        try {
            if (imagenbyte != null) {
                String imagenBase64 = Base64.getEncoder().encodeToString(imagenbyte);
                nuevoEmpledo.setFoto(imagenBase64);
            } else {
                nuevoEmpledo.setFoto(usuarioEdicion.getFoto());
            }
        } catch (Exception ee) {
            System.out.println("Base 64: " + ee.getMessage());
        }
        Call<JsonObject> llamadaModificacion = servicioEmpleado.modificarEmpleado("Bearer " +
                credenciales.getToken(), nuevoEmpledo);
        try {
            Response<JsonObject> respuesta = llamadaModificacion.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    Mensaje.mostrarMensaje(Alert.AlertType.INFORMATION, "Empleado editado",
                            "información de empleado modificado");
                    cerrarVentana();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "error en solicitud",
                            "Error de conexion: " + respuesta.message());
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR",
                        "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "CATCH", e.getMessage());
        }
    }

    private void registrarEmpleado() {
        EmpleadoDominio nuevoEmpledo = new EmpleadoDominio();
        nuevoEmpledo.setNombre(tf_nombre.getText());
        nuevoEmpledo.setApellido(tf_apellido.getText());
        nuevoEmpledo.setContrasena(MetodoCifrado.encriptarCadena(tf_contrasena.getText()));
        nuevoEmpledo.setTelefono(tf_telefono.getText());
        nuevoEmpledo.setTipo("Empleado");
        nuevoEmpledo.setCorreo(tf_correo.getText());
        try {
            String imagenBase64 = Base64.getEncoder().encodeToString(imagenbyte);
            nuevoEmpledo.setFoto(imagenBase64);

        } catch (Exception ee) {
            System.out.println("Base 64: " + ee.getMessage());
        }
        Call<JsonObject> llamadaPost = servicioEmpleado.registrarEmpleado("Bearer " +
                credenciales.getToken(), nuevoEmpledo);
        try {
            Response<JsonObject> respuesta = llamadaPost.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    Mensaje.mostrarMensaje(Alert.AlertType.INFORMATION, "empelado registrado",
                            "Empleado registrado ");
                    cerrarVentana();
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "Error de peticion",
                            "Error de conexipon");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR",
                        "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "CATCH", e.getMessage());
        }
    }

    private boolean correoUnico() {
        boolean correovalido = true;
        Call<JsonObject> llamadaUsuario = servicioCorreo.validarusuaro(tf_correo.getText());
        try {
            Response<JsonObject> respuesta = llamadaUsuario.execute();
            if (respuesta.code() != CodigoOperacion.SIN_PERMISO && respuesta.code() !=
                    CodigoOperacion.ACCESO_DENEGADO) {
                if (respuesta.isSuccessful()) {
                    JsonObject bodyRespuesta = (JsonObject) respuesta.body();
                    int codigo = bodyRespuesta.get("codigo").getAsInt();
                    if (codigo == 200)
                        correovalido = false;
                } else {
                    Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "Error de conexión");
                }
            } else {
                Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "ERROR", "No se cuenta con el acceso");
            }
        } catch (Exception e) {
            Mensaje.mostrarMensaje(Alert.AlertType.ERROR, "CATCH", e.getMessage());
        }
        return correovalido;
    }

    private boolean telefonoValido() {
        boolean valido = true;
        try {
            if (!tf_telefono.getText().isBlank()) {
                double telefono = Double.parseDouble(tf_telefono.getText());
            }
        } catch (Exception e) {
            valido = false;
        }
        return valido;
    }

    public void btn_subirFoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar una imagen del sistema");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de imagen",
                "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            imagenSeleccionada = new Image(archivoSeleccionado.toURI().toString());
            img_foto.setImage(imagenSeleccionada);
            try {
                imagenbyte = Files.readAllBytes(archivoSeleccionado.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package Utilidades;

import javafx.scene.control.Alert;

public class Mensaje {

    public static  void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido)
    {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}

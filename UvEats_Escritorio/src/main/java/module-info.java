module com.example.uveats_escritorio {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    //librerias agregadas
    requires retrofit2;
    requires retrofit2.converter.gson;
    requires retrofit2.converter.scalars;
    requires org.json;
    requires com.google.gson;
   // requires okhttp3;
    exports  Dominio;
    exports Interfaces;

    opens Dominio to javafx.base, com.google.gson;
    opens com.example.uveats_escritorio to javafx.fxml;
    opens DominioRespuesta to javafx.base, com.google.gson;



    exports com.example.uveats_escritorio;
}
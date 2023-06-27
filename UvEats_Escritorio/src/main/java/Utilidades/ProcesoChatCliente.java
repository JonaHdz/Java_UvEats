package Utilidades;

import Dominio.ConversacionDominio;
import DominioRespuesta.LoginRespuesta;
import Interfaces.ConversacionInterface;
import com.example.uveats_escritorio.AtenderPedidoController;
import com.example.uveats_escritorio.NotificacionesCliente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDateTime;

public class ProcesoChatCliente extends Thread {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlPuertos.urlConexion).addConverterFactory(GsonConverterFactory.create()).build();
    private ConversacionInterface servicioConversacion = retrofit.create(ConversacionInterface.class);
    private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    private LoginRespuesta credenciales;

    private ConversacionDominio conversacion;
    private NotificacionesCliente controlador;
    private int idPedido;

    public ProcesoChatCliente(LoginRespuesta credenciales,NotificacionesCliente controlador) {
        this.credenciales = credenciales;
        this.controlador = controlador;

    }




    private volatile boolean detenido = false;  // Bandera de control

    @Override
    public void run() {

        while (!detenido) {  // Verificar la bandera de control
           // System.out.println("corriendo hilo");
            cargarChat();

            try {
                Thread.sleep(2000);  // Utiliza Thread.sleep() en lugar de sleep()
            } catch (InterruptedException e) {

                return;  // Salir del hilo
            }
        }
    }

    public void detenerHilo() {
        detenido = true;  // Establecer la bandera de control como true
        interrupt();  // Interrumpir el hilo si está en estado de espera
    }


    private void cargarChat() {
        Call<JsonObject> llamadaChat = servicioConversacion.recuperarConversacion("Bearer " + credenciales.getToken(), idPedido);
        try {
            Response<JsonObject> respuesta = llamadaChat.execute();
            if (respuesta.isSuccessful()) {
                JsonObject body = (JsonObject) respuesta.body().get("resultado");
                conversacion = gson.fromJson(body, new TypeToken<ConversacionDominio>() {
                }.getType());
                //System.out.println("Conversacion: " + conversacion.getConversacion());
                controlador.tf_chat.setText("");
                controlador.tf_chat.setText(conversacion.getConversacion());
                controlador.conversacion = conversacion;
                //controlador.tf_chat.positionCaret(controlador.tf_chat.getLength());
            } else {
                System.out.println("error en chat: " + respuesta.message());
            }
        } catch (Exception e) {
            System.out.println("catch Conversacion " + e.getMessage());
        }
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
        System.out.println("cre: + " + credenciales.getUsuario().getCorreo() + "- idPedido: " + idPedido);
    }
}




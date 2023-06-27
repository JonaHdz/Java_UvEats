package Interfaces;

import Dominio.ConversacionDominio;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ConversacionInterface {
    @POST("ConversacionPedido/RecuperarConversacionPedido")
    Call<JsonObject> recuperarConversacion(@Header("Authorization") String token,@Body int idPedido);

    @POST("ConversacionPedido/RegistrarConversacion")
    Call<JsonObject> enviarConversacion(@Header("Authorization") String token, @Body ConversacionDominio conversacion);
}

package Interfaces;

import Dominio.ResenaDominio;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ResenaInterface {
    @POST("Resena/RecuperarResenasPorId")
    Call<JsonObject> recuperarResenasPorId(@Header("Authorization") String token,
                                            @Body int id);
    @POST("Resena/RegistrarResena")
    Call<JsonObject> registrarResena(@Header("Authorization") String token,
                                     @Body ResenaDominio resena);
}

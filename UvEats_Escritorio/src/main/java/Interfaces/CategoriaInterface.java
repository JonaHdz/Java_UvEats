package Interfaces;

import Dominio.CategoriaDominio;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

public interface CategoriaInterface {
    @GET("Categoria")
    public Call<JsonObject> recuperarCategoria(@Header("Authorization") String token);

    @POST("Categoria")
    public Call<JsonObject> registrarCategoria(@Header("Authorization") String token, @Body CategoriaDominio categoriaDominio);
    @PUT("Categoria")
    public Call<JsonObject> modificarCategoria(@Header("Authorization") String token, @Body CategoriaDominio categoriaDominio);

}

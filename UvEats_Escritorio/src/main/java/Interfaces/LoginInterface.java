package Interfaces;

import Dominio.LoginDominio;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import java.util.List;

public interface LoginInterface {
    @POST("Login/validarUsuarioLogin")
    Call<JsonObject> iniciarSesion(@Body LoginDominio datosSesion);
}

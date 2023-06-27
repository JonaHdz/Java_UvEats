package Interfaces;

import Dominio.UsuarioDominio;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface UsuarioInterface {
    @POST("Usuario/validarCorreo")
    Call<JsonObject> validarusuaro(@Body String correo);

    @POST("Usuario/RegistrarCliente")
    Call<JsonObject> registrarCliente(@Body UsuarioDominio nuevoCliente);

    @PATCH("Usuario/ActualizarUsuario")
    Call<JsonObject> modificarCliente(@Header("Authorization") String token,
                                        @Body UsuarioDominio usuarioModificado);
    @POST("Usuario/RecuperarClienteId")
    Call<JsonObject> RecuperarClienteId(@Header("Authorization") String token,
                                      @Body int idCliente);

}

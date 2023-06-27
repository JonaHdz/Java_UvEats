package Interfaces;

import Dominio.EmpleadoDominio;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.*;

public interface EmpleadoInterface {
    @GET("Empleado")
    Call<JsonObject> recuperarEmpleados(@Header("Authorization") String token);
    @POST("Empleado")
    Call<JsonObject> registrarEmpleado(@Header("Authorization") String token,@Body EmpleadoDominio empelado);
    @PUT("Empleado")
    Call<JsonObject> modificarEmpleado(@Header("Authorization") String token,@Body EmpleadoDominio empelado);
    @POST("Empleado/EliminarEmpleado")
    Call<JsonObject> eliminarEmpleado(@Header("Authorization") String token, @Body int idEmpleado);


}

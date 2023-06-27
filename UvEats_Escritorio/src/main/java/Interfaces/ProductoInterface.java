package Interfaces;

import Dominio.ProductoDominio;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductoInterface {
    @GET("Producto/RecuperarProductos")
    Call<JsonObject> recuperarProductos(@Header("Authorization") String token);

    @GET("Producto/RecuperarProductosEmpleado")
    Call<JsonObject> recuperarProductosEmpleado(@Header("Authorization") String token);

    @POST("Producto/RegistrarProducto")
    Call<JsonObject> registrarProducto(@Header("Authorization") String token,
                                       @Body ProductoDominio producto);

    @PUT("Producto/ModificarProducto")
    Call<JsonObject> modificarProducto(@Header("Authorization") String token,
                                       @Body ProductoDominio producto);
}

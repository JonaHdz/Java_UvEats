package Interfaces;

import Dominio.PedidoDominio;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.*;

public interface PedidoInterface {
    @POST("Pedido/RegistrarPedidoCliente")
    Call<JsonObject> realizarPedido(@Header("Authorization") String token, @Body PedidoDominio pedido);

    @GET("Pedido/RecuperarPedidosEmpleado")
    Call<JsonObject> recuperarPedidosEmpleado (@Header("Authorization") String token);

    @PUT("Pedido/CambiarEstadoProductoPedido")
    Call<JsonObject> cancelarProductoPedido(@Header("Authorization") String token, @Body int productoPedido);
    @PUT("Pedido/CancelarPedido")
    Call<JsonObject> cancelarPedido(@Header("Authorization") String token, @Body int idPedido);
    @PUT("Pedido/RecogerPedido")
    Call<JsonObject> recogerPedido(@Header("Authorization") String token, @Body int idPedido);

    @POST("Pedido/RecuperarPedidosClienteRecientes")
    Call<JsonObject> recuperarPedidosActualesCliente(@Header("Authorization") String token, @Body int idCliente);

    @POST("Pedido/recuperarHistorialPedidosCliente")
    Call<JsonObject> recuperarHistorialCliente(@Header("Authorization") String token, @Body int idCliente);
}

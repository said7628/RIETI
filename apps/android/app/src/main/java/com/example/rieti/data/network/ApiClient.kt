package com.example.rieti.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Centraliza la configuración de comunicación entre la aplicación Android
 * y la API del sistema RIETI.
 *
 * Utiliza Retrofit para definir el acceso a los servicios HTTP,
 * OkHttp para gestionar las conexiones y Gson para convertir
 * las respuestas JSON en objetos Kotlin.
 *
 * @author Equipo 3:
 *  Lorena Antonio Monraga,
 *  Eduardo Márquez López,
 *  Ángel Adrik Merino Cedeño,
 *  Marco Polo Peña Rivera,
 *  Said Valseca Figueroa.
 */
object ApiClient {
    /**
     * Dirección base de la API de RIETI desplegada mediante
     * una AWS Lambda Function URL.
     */

    private const val BASE_URL =
        "https://tdjmvpcukz452yjfd4pkjuae7i0yajrv.lambda-url.us-east-1.on.aws/"
    /**
     * Interceptor encargado de registrar información básica
     * sobre las solicitudes y respuestas HTTP.
     *
     * El nivel BASIC registra datos generales de la comunicación,
     * como el método, la URL y el código de respuesta,
     * sin registrar los cuerpos de las solicitudes.
     */

    private val logging =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    /**
     * Cliente HTTP utilizado para establecer la comunicación
     * con los servicios del backend.
     *
     * Configura los tiempos máximos de conexión, lectura
     * y escritura, además del interceptor de registro HTTP.
     *
     * Los tiempos establecidos son:
     * - Conexión: 15 segundos.
     * - Lectura: 20 segundos.
     * - Escritura: 20 segundos.
     */

    private val client =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    /**
     * Instancia de la interfaz [RietiApi] utilizada para
     * realizar solicitudes a los endpoints del backend.
     *
     * Retrofit utiliza la dirección base, el cliente OkHttp
     * configurado y Gson para procesar las respuestas JSON.
     */

    val api: RietiApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(RietiApi::class.java)
}

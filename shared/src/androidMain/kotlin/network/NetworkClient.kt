package network

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

class NetworkClient {
    companion object {
        private const val BASE_URL = "http://172.16.200.89:8081/api/"
        fun <T> createService(api: Class<T>): T {
            val builder = OkHttpClient().newBuilder()
            val okHttpClient = builder
                .cookieJar(JavaNetCookieJar(CookieManager()))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create(api)
        }

        fun getBaseUrl(): HttpUrl {
            return BASE_URL.replace("api/", "").toHttpUrl()
        }
    }

}
package alangsatinantongga.md14.kulitku.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit {
    companion object{
        fun getApiService(): UserApi {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                // .addInterceptor(RequestInterceptor(context))
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://uploads-v2-gnkxwtwa6q-uc.a.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(UserApi::class.java)
        }

    }
}
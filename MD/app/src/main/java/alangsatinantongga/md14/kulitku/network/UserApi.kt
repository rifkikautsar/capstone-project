package alangsatinantongga.md14.kulitku.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi {

    @Multipart
    @POST("stories")
    fun postImages (
        @Part file: MultipartBody.Part,
    ) : Call<UploadResponse>

}
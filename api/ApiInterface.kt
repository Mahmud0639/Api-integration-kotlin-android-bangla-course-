package com.manuni.hello_world.api_integration.api

import com.manuni.hello_world.api_integration.models.ResultResponse
import com.manuni.hello_world.api_integration.models.Students
import com.manuni.hello_world.api_integration.models.SubjectsInfo
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiInterface {

    @GET(ApiEndPoints.USER)
    suspend fun getStudents(@Query("currentPage") currentPage: Int, @Query("limit") limit: Int) : List<Students>

    @GET(ApiEndPoints.SUBJECTS)
    suspend fun getSubjects() : List<SubjectsInfo>

    @Multipart
    @POST(ApiEndPoints.UPLOADS)
    suspend fun uploadFile(@Part formData: List<MultipartBody.Part>):ResultResponse

    @POST(ApiEndPoints.USER)
    suspend fun saveStudents(@Body reqBody: Map<String,Any?>):ResultResponse

    @PUT(ApiEndPoints.USER)
    suspend fun updateStudents(@Body reqBody: Map<String, Any?>):ResultResponse

    @PUT(ApiEndPoints.UPLOADS)
    suspend fun updateProfile(@Body reqBody: Map<String, Any?>):ResultResponse

}
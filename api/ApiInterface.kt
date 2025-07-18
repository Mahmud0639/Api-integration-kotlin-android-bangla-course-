package com.manuni.hello_world.api_integration.api

import com.manuni.hello_world.api_integration.models.ResultResponse
import com.manuni.hello_world.api_integration.models.Students
import com.manuni.hello_world.api_integration.models.SubjectsInfo
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
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

    /* If your API requires a body, use @HTTP with hasBody = true. ✅
   If your API only needs an ID as a query parameter, use @DELETE with @Query.*/

    /*@DELETE(AllApi.USER)
    suspend fun deleteUser(@Query("id") userId: Int): retrofit2.Response<ResultModel>*/


    @HTTP(method = "DELETE", path = ApiEndPoints.USER, hasBody = true)
    suspend fun deleteStudent(@Body body: Map<String, Any?>):ResultResponse


    @HTTP(method = "DELETE", path = ApiEndPoints.UPLOADS, hasBody = true)
    suspend fun deleteProfile(@Body body: Map<String, Any?>):ResultResponse

}
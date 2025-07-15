package com.manuni.hello_world.api_integration.api

import com.manuni.hello_world.api_integration.models.Students
import com.manuni.hello_world.api_integration.models.Subjects
import com.manuni.hello_world.api_integration.models.SubjectsInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET(ApiEndPoints.USER)
    suspend fun getStudents(@Query("currentPage") currentPage: Int, @Query("limit") limit: Int) : List<Students>

    @GET(ApiEndPoints.SUBJECTS)
    suspend fun getSubjects() : List<SubjectsInfo>
}
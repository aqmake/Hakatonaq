package com.example.hakaton.data

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate
import retrofit2.http.Header
import retrofit2.http.DELETE

interface Api {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @GET("excursions/")
    suspend fun getExcursions(): Response<List<Excursion>>

    @GET("groups/")
    suspend fun getGroups(@Query("phone") phone: String): Response<List<GroupResponse>>

    @POST("groups/")
    suspend fun addGroup(@Body body: AddGroupRequest): Response<GroupResponse>

    @GET("groups/summary")
    suspend fun getGroupsSummary(
        @Header("X-Staff-Code") staffCode: String
    ): Response<List<GroupSummaryResponse>>

    @GET("participants/{groupId}")
    suspend fun getParticipants(@Path("groupId") groupId: Long): Response<List<ParticipantResponse>>

    @POST("participants/{groupId}")
    suspend fun addParticipant(
        @Path("groupId") groupId: Long,
        @Body body: AddParticipantRequest
    ): Response<ParticipantResponse>

    @PATCH("participants/checkin/{passCode}")
    suspend fun checkIn(
        @Path("passCode") passCode: String,
        @Header("X-Staff-Code") staffCode: String
    ): Response<CheckInResponse>

    @DELETE("excursions/{id}")
    suspend fun deleteExcursion(
        @Path("id") id: Long,
        @Header("X-Staff-Code") staffCode: String
    ): Response<Unit>

    @POST("excursions/")
    suspend fun addExcursion(
        @Header("X-Staff-Code") staffCode: String,
        @Body body: AddExcursionRequest
    ): Response<Excursion>
}


data class AddExcursionRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("startsAt") val startsAt: LocalDate
)

data class Excursion(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("startsAt") val startsAt: LocalDate
)


data class LoginRequest(
    @SerializedName("passCode") val passCode: String
)

data class AddGroupRequest(
    @SerializedName("schoolName") val schoolName: String,
    @SerializedName("visitDate") val visitDate: LocalDate,
    @SerializedName("contactPhone") val contactPhone: String,
    @SerializedName("excursionId") val excursionId: Long
)

data class AddParticipantRequest(
    @SerializedName("fullName") val fullName: String
)

data class AuthResponse(
    @SerializedName("role") val role: String,
    @SerializedName("participant") val participant: ParticipantWithGroup?
)

data class ParticipantWithGroup(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("passCode") val passCode: String,
    @SerializedName("checkedIn") val checkedIn: Boolean,
    @SerializedName("group") val group: GroupResponse
)

data class ParticipantResponse(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("passCode") val passCode: String,
    @SerializedName("checkedIn") val checkedIn: Boolean
)

data class GroupResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("schoolName") val schoolName: String,
    @SerializedName("visitDate") val visitDate: LocalDate,
    @SerializedName("excursionResponse") val excursion: Excursion
)

data class GroupSummaryResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("schoolName") val schoolName: String,
    @SerializedName("visitDate") val visitDate: LocalDate,
    @SerializedName("contactPhone") val contactPhone: String,
    @SerializedName("totalParticipants") val totalParticipants: Long,
    @SerializedName("checkedInParticipants") val checkedInParticipants: Long
)

data class CheckInResponse(
    @SerializedName("message") val message: String
)

data class ErrorResponse(
    @SerializedName("message") val message: String
)
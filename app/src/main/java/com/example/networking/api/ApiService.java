package com.example.networking.api;

import com.example.networking.model.AddMahasiswaResponse;
import com.example.networking.model.MahasiswaResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("mahasiswa")
    Call<MahasiswaResponse> getMahasiswa(@Query("nrp") String nrp);

    @GET("mahasiswa")
    Call<MahasiswaResponse> getAllMahasiswa();

    @POST("mahasiswa")
    @FormUrlEncoded
    Call<AddMahasiswaResponse> addMahasiswa(
            @Field("nrp") String nrp,
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("jurusan") String jurusan
    );

    @PUT("mahasiswa/{id}")
    @FormUrlEncoded
    Call<AddMahasiswaResponse> updateMahasiswa(
            @Path("id") String id,
            @Field("nrp") String nrp,
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("jurusan") String jurusan
    );

    @DELETE("mahasiswa/{id}")
    Call<AddMahasiswaResponse> deleteMahasiswa(@Path("id") String id);
}
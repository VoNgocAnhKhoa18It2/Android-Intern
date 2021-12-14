package com.vnakhoa.midtest_intern;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Service {
    @FormUrlEncoded
    @POST("/api/v4/login")
    Call<ResponseBody> login(@Field("id") String id,
                             @Field("password") String password);
    @FormUrlEncoded
    @POST("/api/v4/join")
    Call<ResponseBody> join(@Field("id") String id,
                            @Field("password") String password,
                            @Field("email") String email,
                            @Field("birth") String birth);

    @GET("/api/v4/get-magic")
    Call<ResponseBody> getMagic();

    @FormUrlEncoded
    @POST("/api/v4/check-id")
    Call<ResponseBody> checkID(@Field("id") String id);

}

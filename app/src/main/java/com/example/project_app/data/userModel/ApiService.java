package com.example.project_app.data.userModel;


import android.location.Location;

import com.example.project_app.Mapdata.LocationSearch;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.project_app.PotholeDataByMonth;
import com.example.project_app.ReceiveHoursOfUse;
import com.example.project_app.RecievedDistance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://9404-113-161-66-26.ngrok-free.app/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("users/login")
    Call<NormalLoginResponse> login(@Body NormalLoginRequest normalLoginRequest);

    @POST("users/google-sign-in")
    Call<LoginWithGoogleResponse> google_sign_in(@Body LoginWithGoogleRequest loginWithGoogleRequest);

    @POST("users/facebook-sign-in")
    Call<LoginWithFacebookResponse> facebook_sign_in(@Body LoginWithFacebookRequest loginWithFacebookRequest);

    @POST("users/register")
    Call<NormalLoginResponse> sign_up(@Body SignUpRequest signUpRequest );

    @Multipart
    @POST("potholes/create")
    Call<MapPotholeinfo>createPothole(@Header("Authorization") String token, @Query("_id") String user_id,
            @Part MultipartBody.Part image, @Part("mapPotholeinfo") RequestBody mapPotholeinfo);

    @GET("potholes")
    Call<PotholeResponse> getPotholes(@Header("Authorization") String token);

    @POST("users/forgot-password")
    Call<ForgotPasswordResponse> forgot_password(@Body ForgotPasswordRequest forgotPasswordRequest );

    @POST("users/verify")
    Call<ForgotPasswordResponse> verify(@Body VerificationRequest verificationRequest);

    @PUT("users/edit")
    Call<ForgotPasswordResponse> update_password(@Header("Authorization") String token,@Body UpdatePasswordRequest updatePasswordRequest );

    @GET("history")
    Call<HistoryResponse> getHistory(@Header("Authorization") String token, @Query("id") String id,@Query("page") int page, @Query("limit") int limit);

    @GET("locations")
    Call<List<LocationSearch>>getLocationSearch(@Query("keyword") String keyword);

    @GET("potholes/month")
    Call<PotholeDataByMonth> getPotholeDataByMonth(@Header("Authorization") String token,@Query("_id") String _id, @Query("month") String month, @Query("year") String year);

    @GET("monitor")
    Call<ReceiveHoursOfUse> getHoursOfUseData(@Header("Authorization") String token, @Query("type") String type, @Query("_id") String id, @Query("month") String month, @Query("year") String year);

    @GET("monitor")
    Call<RecievedDistance> getDistance(@Header("Authorization") String token, @Query("type") String type, @Query("_id") String id, @Query("month") String month, @Query("year") String year);

    @PUT("users/edit")
    Call<UserSignInWithGoogle> updateProfile(@Header("Authorization") String token, @Query("type_update") String type_update, @Query("type_user") String type_user, @Body UserSignInWithGoogle user);
    @PUT("users/update-preferences")
    Call<Void> updatePreferences( @Query("type_user") String type_user, @Body UpdatePreferencesRequest request );
    @GET("route")
    Call<RoutingResponse> getPoint(
            @Query("start") String start,
            @Query("end") String end
    );
    @GET("download-map")
    Call<ResponseBody>getMap();

    @POST("monitor")
    Call<String>updateData(@Body Monitor monitor);
  
    @GET("potholes/ById")
    Call<potholeResponseDetail>getPotholesById(@Query("_id") String id);

    @GET("potholes/address")
    Call<String> getAddress(@Header("Authorization") String token, @Query("latitude") double latitude, @Query("longitude") double longitude);

    @GET("potholes/ByIdUser")
    Call<List<MapPotholeinfo2>>getPotholesByIdUser(@Query("id") String id);
}

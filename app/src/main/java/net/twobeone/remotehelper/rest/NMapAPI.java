package net.twobeone.remotehelper.rest;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NMapAPI {

    @GET("local.json")
    Call<NMap> local(@Header("X-Naver-Client-Id") String clientId, @Header("X-Naver-Client-Secret") String clientSecret, @Query(value = "query") String query);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/v1/search/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}


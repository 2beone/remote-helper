package net.twobeone.remotehelper.rest;

import net.twobeone.remotehelper.rest.model.NaverMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface NaverMapAPI {

    @Headers({
            "X-Naver-Client-Id: vaaipy79LqtPJRueO9eJ",
            "X-Naver-Client-Secret: y6UNfzsFFK"
    })
    @GET("local.json")
    Call<NaverMap> geocode(@Query(value = "query") String query);
    // Call<NaverMap> geocode(@Query(value = "query") String query);
    // Call<String> geocode(@Header("X-Naver-Client-Id") String clientId, @Header("X-Naver-Client-Secret") String clientSecret, @QueryMap(encoded = true) Map<String, String> options);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/v1/search/") // https://openapi.naver.com/v1/map/geocode
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

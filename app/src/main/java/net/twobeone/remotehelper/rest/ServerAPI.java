package net.twobeone.remotehelper.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface ServerAPI {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://remohelper.com:440/m/api/") // https://remohelper.com:440/m/api/ver.do
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

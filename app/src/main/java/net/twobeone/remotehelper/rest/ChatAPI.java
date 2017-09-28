package net.twobeone.remotehelper.rest;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.util.StringConverterFactory;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatAPI {

    @POST("{uid}/{botId}")
    Call<String> join(@Path("uid") String uid, @Path("botId") String botId);

    @PUT("{uid}/{botId}")
    Call<String> send(@Path("uid") String uid, @Path("botId") String botId, @Query(value = "message") String message);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.HTTP_URI_CHAT)
            .addConverterFactory(new StringConverterFactory())
            // .addConverterFactory(GsonConverterFactory.create())
            .build();
}

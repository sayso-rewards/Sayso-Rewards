package com.sayso.retrofit;

import com.sayso.ui.model.SurveyAvailabilityModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("survey/{partnerId}")
    Call<ResponseBody> survey(@Path("partnerId") String partnerId, @Query("rid") String rid);

    @GET("issurveyavailable/{partnerId}")
    Call<SurveyAvailabilityModel> isSurveyAvailable(@Path("partnerId") String partnerId, @Query("rid") String rid);
}

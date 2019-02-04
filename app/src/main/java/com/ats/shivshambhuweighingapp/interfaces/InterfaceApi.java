package com.ats.shivshambhuweighingapp.interfaces;

import com.ats.shivshambhuweighingapp.model.Contractor;
import com.ats.shivshambhuweighingapp.model.GetPoklenReadingList;
import com.ats.shivshambhuweighingapp.model.GetWeighingList;
import com.ats.shivshambhuweighingapp.model.Info;
import com.ats.shivshambhuweighingapp.model.LoginResponse;
import com.ats.shivshambhuweighingapp.model.PoklenReading;
import com.ats.shivshambhuweighingapp.model.SubPlant;
import com.ats.shivshambhuweighingapp.model.Vehicle;
import com.ats.shivshambhuweighingapp.model.Weighing;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface InterfaceApi {

    @POST("loginUser")
    Call<LoginResponse> doLogin(@Query("usrMob") String usrMob, @Query("userPass") String userPass);

    @POST("getVehListByVehicleType")
    Call<ArrayList<Vehicle>> getVehiclelistByType(@Query("vehicleType") int vehicleType);

    @GET("getAllSubPlantList")
    Call<ArrayList<SubPlant>> getAllSubPlantList();


    @POST("savePoklenReading")
    Call<PoklenReading> savePoklenReading(@Body PoklenReading poklenReading);

    @POST("getPokReadingListBetweenDate")
    Call<ArrayList<GetPoklenReadingList>> getPoklenReadingList(@Query("fromDate") String fromDate, @Query("toDate") String toDate);

    @POST("deletePoklenReading")
    Call<Info> deletePoklenReading(@Query("readingId") int readingId);

    @GET("getAllContractorList")
    Call<ArrayList<Contractor>> getContractorList();

    @POST("getContractorById")
    Call<Contractor> getContractorById(@Query("contrId") int contrId);

    @POST("saveWeighing")
    Call<Weighing> saveWeighing(@Body Weighing weighing);

    @POST("getWeighListBetweenDate")
    Call<ArrayList<GetWeighingList>> getWeighingList(@Query("contrId") int contrId, @Query("vehicleId") int vehicleId, @Query("fromDate") String fromDate, @Query("toDate") String toDate);

    @Multipart
    @POST("photoUpload")
    Call<JSONObject> imageUpload(@Part MultipartBody.Part file, @Part("imageName") RequestBody name, @Part("type") RequestBody type);

    @POST("deleteWeighing")
    Call<Info> deleteWeight(@Query("weighId") int weighId);


}

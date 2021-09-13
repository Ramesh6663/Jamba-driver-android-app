package com.jambacabs.driver.api;

import com.jambacabs.driver.models.AcceptRejectModel;
import com.jambacabs.driver.models.AddVehiclePostBean;
import com.jambacabs.driver.models.CommonResponseBean;
import com.jambacabs.driver.models.CustomerProfileBean;
import com.jambacabs.driver.models.DriverDataBean;
import com.jambacabs.driver.models.DriverVehicleModel;
import com.jambacabs.driver.models.DynamicPagesBean;
import com.jambacabs.driver.models.EarningPostBean;
import com.jambacabs.driver.models.EarningsResponseBean;
import com.jambacabs.driver.models.HeatMapBaseResponse;
import com.jambacabs.driver.models.ImageUploadBean;
import com.jambacabs.driver.models.ResetPassPostBean;
import com.jambacabs.driver.models.SettingsModel;
import com.jambacabs.driver.models.StatusModel;
import com.jambacabs.driver.models.TransactionResponseBean;
import com.jambacabs.driver.models.UpdateDriverBean;
import com.jambacabs.driver.models.UpdateVehiclePostBean;
import com.jambacabs.driver.singleton.Constants;

import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static com.jambacabs.driver.singleton.Constants.SETTINGS;

public interface ApiInterface {
    @Multipart
    @POST(Constants.IMAGE_UPLOAD)
    Call<ImageUploadBean> imageUpload(@Header("x-custom-token") String token, @Part MultipartBody.Part imageFile);

    @Headers("Accept: application/json")
    @POST(Constants.UPDATE_DRIVER_DATA)
    Call<CommonResponseBean> updateDriver(@Header("x-custom-token") String token, @Body UpdateDriverBean updateDriverBean);

    @Headers("Accept: application/json")
    @POST(Constants.UPDATE_DRIVER_DATA)
    Call<CommonResponseBean> updateDriverStatus(@Header("x-custom-token") String token, @Body StatusModel statusModel);

    @GET(Constants.GET_PARTICULAR_RIDE)
    Call<DriverDataBean> getParticularDriver(@Header("x-custom-token") String token, @Path("id") String driverId);

    @GET(Constants.GET_DRIVER_TRANSACTIONS)
    Call<TransactionResponseBean> getTransactions(@Header("x-custom-token") String token, @Path("driverId") Long driverId);

    @Headers("Accept: application/json")
    @POST(Constants.GET_DRIVER_EARNINGS)
    Call<EarningsResponseBean> getDriverEarnings(@Header("x-custom-token") String token, @Body EarningPostBean earningPostBean);

    @Headers("Accept: application/json")
    @POST(Constants.ADD_VEHICLES)
    Call<CommonResponseBean> addVehicle(@Header("x-custom-token") String token, @Body AddVehiclePostBean updateDriverBean);

    @Headers("Accept: application/json")
    @POST(Constants.UPDATE_VEHICLES)
    Call<CommonResponseBean> updateVehicle(@Header("x-custom-token") String token, @Body UpdateVehiclePostBean updateVehiclePostBean);

    @Headers("Accept: application/json")
    @POST(Constants.FORGOT_PASSWORD)
    Call<CommonResponseBean> forgotPassword(@Body HashMap<String, String> data);

    @Headers("Accept: application/json")
    @POST(Constants.RESET_PASSWORD)
    Call<CommonResponseBean> resetPassword(@Body ResetPassPostBean data);

    @GET(Constants.GET_CUSTOMER_PROFILE)
    Call<CustomerProfileBean> getCustomerProfile(@Header("x-custom-token") String token, @Path("id") Long driverId);

    @GET(Constants.GET_PARTICULAR_RIDE)
    Call<CustomerProfileBean> getCustomerProfileNew(@Header("x-custom-token") String token, @Path("id") Long driverId);

    @GET(Constants.GET_DYNAMIC_PAGES_NEW)
    Call<DynamicPagesBean> getDynamicPages(@Header("x-custom-token") String token);

    @GET(Constants.GET_PERTICULAR_VEHICLE)
    Call<DriverVehicleModel> getPerticularVehicle(@Header("x-custom-token") String token, @Path("id") String vehicle_id);

    @GET(Constants.VEHICLES_NEW)
    Call<DriverVehicleModel> getVehicles(@Header("x-custom-token") String token, @Path("id") String driver_id);

    @GET(SETTINGS)
    Call<SettingsModel> getSettings(@Header("x-custom-token") String token);

    @GET(Constants.GET_HEATMAPDATA)
    Call<HeatMapBaseResponse> getHeatMapData(@Header("x-custom-token") String token, @Path("usertype") String userType,
                                             @Path("online") String online);
    @Headers("Accept: application/json")
    @POST(Constants.ACCEPT_NOTIFICATON)
    Call<CommonResponseBean> acceptNotification(@Header("x-custom-token") String token, @Body AcceptRejectModel model);

    @Headers("Accept: application/json")
    @POST(Constants.REJECT_NOTIFICATON)
    Call<CommonResponseBean> rejectNotification(@Header("x-custom-token") String token, @Body AcceptRejectModel model);
}

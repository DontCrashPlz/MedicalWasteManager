package com.hnsi.zheng.medicalwastemanager.https;

import com.hnsi.zheng.medicalwastemanager.beans.CollectedWasteEntity;
import com.hnsi.zheng.medicalwastemanager.beans.InputedBucketEntity;
import com.hnsi.zheng.medicalwastemanager.beans.WasteUploadEntity;
import com.hnsi.zheng.medicalwastemanager.beans.HttpResult;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Zheng on 2018/4/22.
 */

public interface ApiService {

    /***************************收集***************************/

    @GET("/hospital/mobile/ylfpgl/medicalWaste/collect/add.do")
    Observable<HttpResult<WasteUploadEntity>> collectMedicalWaste(@Query("departmentUserId") String departmentUserId,//科室人员
                                                                  @Query("weight") String weight,//重量
                                                                  @Query("wasteTypeDictId") String wasteTypeDictId,//医废类型id
                                                                  @Query("userId") String userId,//入库人id
                                                                  @Query("orgId") String orgId,//医院id
                                                                  @Query("guid") String guid);//标签号

    @GET("/hospital/mobile/ylfpgl/medicalWaste/collect/query.do")
    Observable<HttpResult<ArrayList<CollectedWasteEntity>>> getcollectedList(@QueryMap Map<String, String> queryMap);

    @GET("/hospital/mobile/ylfpgl/medicalWaste/collect/del.do")
    Observable<HttpResult<String>> deleteCollectedWaste(@Query("orgId") String orgId, @Query("guid") String guid);



    /***************************入库***************************/

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hospital/mobile/ylfpgl/medicalWaste/input/add.do?")
    Observable<HttpResult<String>> inputWastes(@Body RequestBody requestBody);

    @GET("/hospital/mobile/ylfpgl/medicalWaste/input/del.do")
    Observable<HttpResult<String>> deleteInputedBucket(@Query("orgId") String orgId, @Query("userId") String userId, @Query("guid") String guid);

    @GET("/hospital/mobile/ylfpgl/medicalWaste/input/query.do")
    Observable<HttpResult<ArrayList<InputedBucketEntity>>> getInputedList(@QueryMap Map<String, String> queryMap);

    @GET("/hospital/mobile/ylfpgl/medicalWaste/input/get.do")
    Observable<HttpResult<InputedBucketEntity>> getInputedBucketInfo(@QueryMap Map<String, String> queryMap);

    @GET("/hospital/mobile/ylfpgl/medicalWaste/input/delWaste.do")
    Observable<HttpResult<String>> deleteInputedWaste(@Query("orgId") String orgId, @Query("guid") String guid, @Query("wasteGuid") String wasteGuid);



    /***************************出库***************************/

    @GET("/hospital/mobile/ylfpgl/medicalWaste/output/edit.do")
    Observable<HttpResult<String>> outputWasteBucket(@Query("orgId") String orgId, @Query("userId") String userId, @Query("fileIds") String fileIds, @Query("outputTotalWeight") String outputTotalWeight);

    @GET("/hospital/mobile/ylfpgl/medicalWaste/output/query.do")
    Observable<HttpResult<String>> getOutputedList(@Query("orgId") String orgId, @Query("userId") String userId, @Query("key") String key);

    @GET("/hospital/mobile/ylfpgl/medicalWaste/output/get.do")
    Observable<HttpResult<String>> getOutputDetail(@Query("orgId") String orgId, @Query("userId") String userId, @Query("guid") String guid);

}

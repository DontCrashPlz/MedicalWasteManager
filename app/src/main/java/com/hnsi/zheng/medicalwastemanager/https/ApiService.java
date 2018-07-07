package com.hnsi.zheng.medicalwastemanager.https;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Zheng on 2018/4/22.
 */

public interface ApiService {
    @GET("/mobile/ylfpgl/medicalWaste/collect/add.do")
    Observable<String> collectMedicalWaste(@Query("departmentUserId") String departmentUserId,
                                           @Query("weight") String weight,
                                           @Query("wasteTypeDictId") String wasteTypeDictId,
                                           @Query("userId") String userId,
                                           @Query("orgId") String orgId,
                                           @Query("guid") String guid);
}

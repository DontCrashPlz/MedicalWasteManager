package com.hnsi.zheng.medicalwastemanager.https;

import android.support.annotation.Nullable;

import com.hnsi.zheng.medicalwastemanager.beans.CollectedWasteEntity;
import com.hnsi.zheng.medicalwastemanager.beans.InputedBucketEntity;
import com.hnsi.zheng.medicalwastemanager.beans.UploadPhotoEntity;
import com.hnsi.zheng.medicalwastemanager.beans.WasteUploadEntity;
import com.hnsi.zheng.medicalwastemanager.beans.HttpResult;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zheng on 2018/4/23.
 */

public class Network {

    private static Network mInstance= null;

    public static Network getInstance(){
        if (mInstance== null){
            synchronized (Network.class){
                if (mInstance== null){
                    mInstance= new Network();
                }
            }
        }
        return mInstance;
    }

    private Network(){
        if (apiService == null) {
            if (mOkHttpClient== null){
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                mOkHttpClient= new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15,TimeUnit.SECONDS)
                        .addInterceptor(logging)
                        .build();
            }
            if (mRetrofit== null){
                mRetrofit= new Retrofit.Builder()
                        .client(mOkHttpClient)
                        .baseUrl(BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
            }
            apiService = mRetrofit.create(ApiService.class);
        }
    }

    private static ApiService apiService;
    private static OkHttpClient mOkHttpClient;
    private static Retrofit mRetrofit;
    //private static final String BASEURL= "http://47.96.81.0/";
    //private static final String BASEURL= "http://192.168.12.150:8080/";
    private static final String BASEURL= "http://dns.37371.cn:8016/";

    /**
     * 收集医废打印标签后调用次接口
     * @param departmentUserId
     * @param weight
     * @param wasteTypeDictId
     * @param userId
     * @param orgId
     * @param guid
     * @return
     */
    public Observable<HttpResult<WasteUploadEntity>> collectMedicalWaste(String departmentUserId,//科室人员
                                                                         String weight,//重量
                                                                         String wasteTypeDictId,//医废类型id
                                                                         String userId,//入库人id
                                                                         String orgId,//医院id
                                                                         String guid){//医废编号
        return apiService.collectMedicalWaste(departmentUserId, weight, wasteTypeDictId, userId, orgId, guid);
    }

    /**
     * 获取已收集的医废列表
     * @param orgId
     * @param userId
     * @param key
     * @return
     */
    public Observable<HttpResult<ArrayList<CollectedWasteEntity>>> getCollectedList(String orgId,//医院id
                                                                                    String userId,//收集人id
                                                                                    @Nullable String key){//关键字
        Map<String, String> paramsMap= new HashMap<>();
        paramsMap.put("orgId", orgId);
        paramsMap.put("userId", userId);
        if (key!= null && key.trim().length()> 0){
            paramsMap.put("key", key);
        }
        return apiService.getcollectedList(paramsMap);
    }

    /**
     * 删除已收集的医废列表
     * @param orgId
     * @param guid
     * @return
     */
    public Observable<HttpResult<String>> deleteCollectedWaste(String orgId, String guid){
        return apiService.deleteCollectedWaste(orgId, guid);
    }

    /**
     * 上传入库信息
     * @param jsonObject
     * @return
     */
    public Observable<HttpResult<String>> inputMedicalWaste(JSONObject jsonObject){
        RequestBody body= RequestBody.create(MediaType.parse("application/json"),jsonObject.toString());
        return apiService.inputWastes(body);
    }

    /**
     * 删除已入库的医废桶
     * @param orgId
     * @param userId
     * @param guid
     * @return
     */
    public Observable<HttpResult<String>> deleteInputedBucket(String orgId, String userId, String guid, String id){
        return apiService.deleteInputedBucket(orgId, userId, guid, id);
    }

    /**
     * 获取已入库的医废桶列表
     * @param orgId
     * @param userId
     * @param key
     * @return
     */
    public Observable<HttpResult<ArrayList<InputedBucketEntity>>> getInputedList(String orgId,//医院id
                                                                                 String userId,//收集人id
                                                                                 @Nullable String key){//关键字
        Map<String, String> paramsMap= new HashMap<>();
        paramsMap.put("orgId", orgId);
        paramsMap.put("userId", userId);
        if (key!= null && key.trim().length()> 0){
            paramsMap.put("key", key);
        }
        return apiService.getInputedList(paramsMap);
    }

    /**
     * 获取已入库的医废桶详情
     * @param orgId
     * @param userId
     * @param guid
     * @return
     */
    public Observable<HttpResult<InputedBucketEntity>> getInputedBucketInfo(String orgId, String userId, String guid, @Nullable String key, String id){
        Map<String, String> paramsMap= new HashMap<>();
        paramsMap.put("orgId", orgId);
        paramsMap.put("userId", userId);
        paramsMap.put("guid", guid);
        if (key!= null && key.trim().length()> 0){
            paramsMap.put("key", key);
        }
        paramsMap.put("id", id);
        return apiService.getInputedBucketInfo(paramsMap);
    }

    /**
     * 删除已入库的医废袋
     * @param orgId
     * @param guid
     * @param wasteGuid
     * @return
     */
    public Observable<HttpResult<String>> deleteInputedWaste(String orgId, String guid, String wasteGuid){
        return apiService.deleteInputedWaste(orgId, guid, wasteGuid);
    }

    /**
     * 获取要出库的医废桶的入库净重
     * @param guid
     * @return
     */
    public Observable<HttpResult<InputedBucketEntity>> getBucketDetail(String guid){
        return apiService.getBucketDetail(guid);
    }

    /**
     * 上传出库信息
     * @param jsonObject
     * @return
     */
    public Observable<HttpResult<String>> outputWasteBucket(JSONObject jsonObject){
        RequestBody body= RequestBody.create(MediaType.parse("application/json"),jsonObject.toString());
        return apiService.outputWasteBucket(body);
    }

    /**
     * 获取已出库的医废桶列表
     * @param orgId
     * @param userId
     * @param key
     * @return
     */
    public Observable<HttpResult<ArrayList<InputedBucketEntity>>> getOutputedList(String orgId,//医院id
                                                                                  String userId,//收集人id
                                                                                  @Nullable String key){//关键字
        Map<String, String> paramsMap= new HashMap<>();
        paramsMap.put("orgId", orgId);
        paramsMap.put("userId", userId);
        if (key!= null && key.trim().length()> 0){
            paramsMap.put("key", key);
        }
        return apiService.getOutputedList(paramsMap);
    }

    /**
     * 上传图片
     * @param userId
     * @param file
     * @return
     */
    public Observable<HttpResult<UploadPhotoEntity>> uploadPhoto(String userId, File file){
        //2、创建RequestBody，其中`multipart/form-data`为编码类型
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        //3、创建`MultipartBody.Part`，其中需要注意第一个参数`fileUpload`需要与服务器对应,也就是`键`
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return apiService.uploadPhoto(userId, part);
    }

}

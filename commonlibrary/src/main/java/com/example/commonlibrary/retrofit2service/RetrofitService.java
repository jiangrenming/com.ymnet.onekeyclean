package com.example.commonlibrary.retrofit2service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.commonlibrary.retrofit2service.api.GithubApi;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit2Client;
import utils.ConvertParamsUtils;

/**
 * Created by jrm on 2017-3-29.
 * 初始化retrofit的入口处,如果需要初始化多个不同的API的话，可以在这里进行自行添加
 */

public class RetrofitService extends Retrofit2Client {

   public  static  RetrofitService service;
   public  static  GithubApi       githubApi;

   private Handler retroHandle;

    public  static  RetrofitService getInstance(){
        if (service == null){
            service = new RetrofitService();
        }
        return  service;
    }

    public RetrofitService(){
        githubApi =  retrofitBuilder.baseUrl(GithubApi.BASE_URL).build().create(GithubApi.class);
        this.retroHandle = new Handler(Looper.getMainLooper());
    }


    /**
     * 以下的方法及接口主要针对如下情况使用：
     * 当返回的数据结构并不是按照统一格式时，需要使用一下的方法来代替之前的方法，若使用下面的方法，需要从最外层开始创建数据模型
     * 请谨慎使用~
     *
     */
    public interface MyCallBack{
       <T> void onSucess(T object);
        void onFailure(String error);
    }

   /* *
     * 直接添加参数的方式
     */
    public void postAppInfoParams(final MyCallBack myCallBack){
        Call<Object> folder = githubApi.createFolder(1, 1, 32);
        folder.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(final Call<Object> call, final Response<Object> response) {
                Log.i("tag---",response.body().toString());
                retroHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (myCallBack != null){
                            myCallBack.onSucess(response.body());
                            if (call != null){
                                call.cancel();
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                String message = t.getMessage();
                if (myCallBack != null) {
                    myCallBack.onFailure(message);
                }
            }
        });
    }

     /*
     * map的方式添加参数
     * */
    public void postAppInfoMapParams(final MyCallBack myCallBack,int folderId,int pageSize,int pageNumber){
        Map<String, String> params = ConvertParamsUtils.getInstatnce().getParamsThree("floderId", String.valueOf(folderId)
                                         ,"pageSize", String.valueOf(pageSize),"pageNumber", String.valueOf(pageNumber));
        githubApi.createFolderMap(params).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(final Call<Object> call, final Response<Object> response) {
                retroHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (myCallBack != null){
                            myCallBack.onSucess(response.body());
                            if (call != null){
                                call.cancel();
                            }
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                String message = t.getMessage();
                if (myCallBack != null) {
                    myCallBack.onFailure(message);
                }
            }
        });
    }

    /**
     * 信息流的获取
     * @param myCallBack
     * @param params
     */
    public void postAppInfoInformations(final MyCallBack myCallBack,Map<String,String > params){
        githubApi.createBaiduInfomations(params).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(final Call<Object> call, final Response<Object> response) {
                retroHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (myCallBack != null){
                            myCallBack.onSucess(response.body());
                            if (call != null){
                                call.cancel();
                            }
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                String message = t.getMessage();
                if (myCallBack != null) {
                    myCallBack.onFailure(message);
                }
            }
        });
    }
}

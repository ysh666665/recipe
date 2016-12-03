package com.ysh.demo.demo1.retrofit;

import com.ysh.demo.demo1.gsonbean.CategoryBean;
import com.ysh.demo.demo1.gsonbean.SearchResultBean;
import com.ysh.demo.demo1.gsonbean.SingleMenu;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hasee on 2016/11/18.
 */
public interface MenuApi {
    @GET("category/query")//请求目录
    Observable<CategoryBean> getCategory(@Query("key")String key);


    @GET("menu/search")//通过输入关键词搜索
    Observable<SearchResultBean> getMenuFreely(@Query("key")String key,@Query("name")String name,@Query("page")int page);

    @GET("menu/search")//通过选择标签搜索
    Observable<SearchResultBean> getMenuByCid(@Query("key")String key, @Query("cid")String cid,@Query("page")int page);

    @GET("menu/query")//通过选择标签搜索
    Observable<SingleMenu> getMenuById(@Query("key")String key, @Query("id")String cid);




}

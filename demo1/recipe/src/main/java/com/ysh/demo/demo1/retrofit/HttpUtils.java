package com.ysh.demo.demo1.retrofit;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by hasee on 2016/11/21.
 */

public class HttpUtils {

    private final String base_url = "http://apicloud.mob.com/v1/cook/";
    private final String accessing_key = "18f622bb20818";
    public static final int SEARCH_ALL_LABLE = 1;
    public static final int SEARCH_FREELY = 2;
    public static final int SEARCH_BY_LABLE_ID = 3;
    public static final int SEARCH_BY_MENU_ID = 4;
    private Retrofit retrofit;
    private MenuApi menuCategory;


    private HttpUtils() {
        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        menuCategory = retrofit.create(MenuApi.class);
    }

    private static class Holder {
        private static final HttpUtils instance = new HttpUtils();
    }

    public static HttpUtils getInstance(){
        return Holder.instance;
    }

    @IntDef({SEARCH_ALL_LABLE, SEARCH_FREELY, SEARCH_BY_LABLE_ID,SEARCH_BY_MENU_ID})
    @interface SearchCode {}

    public Observable<?> getCall(@SearchCode int requestCode, @Nullable String searchContent,@Nullable int page) {
        switch (requestCode) {
            case SEARCH_ALL_LABLE:
                return menuCategory.getCategory(accessing_key);
            case SEARCH_FREELY:
                return menuCategory.getMenuFreely(accessing_key, searchContent,page);
            case SEARCH_BY_LABLE_ID:
                return menuCategory.getMenuByCid(accessing_key, searchContent,page);
            case SEARCH_BY_MENU_ID:
                return menuCategory.getMenuById(accessing_key, searchContent);
        }
        return null;
    }
}

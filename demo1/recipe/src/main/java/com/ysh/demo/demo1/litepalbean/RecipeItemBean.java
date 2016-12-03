package com.ysh.demo.demo1.litepalbean;

import org.litepal.crud.DataSupport;

/**
 * Created by hasee on 2016/11/27.
 */

public class RecipeItemBean extends DataSupport{
    private String imageUri;
    private String title;
    private String describe;
    private String menuId;

    public RecipeItemBean() {
    }

    public RecipeItemBean(String imageUri, String title, String describe, String menuId) {
        this.imageUri = imageUri;
        this.title = title;
        this.describe = describe;

        this.menuId = menuId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}

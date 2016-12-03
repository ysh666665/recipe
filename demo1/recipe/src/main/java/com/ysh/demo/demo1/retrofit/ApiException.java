package com.ysh.demo.demo1.retrofit;

/**
 * Created by hasee on 2016/11/23.
 */

public class ApiException extends RuntimeException {

    private int retCode;

    public ApiException(int retCode) {
        super();
        this.retCode = retCode;
    }

    public int getRetCode() {
        return retCode;
    }
}

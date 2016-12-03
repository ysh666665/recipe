package com.ysh.demo.demo1.litepalbean;

import org.litepal.crud.DataSupport;

/**
 * Created by hasee on 2016/11/30.
 */

public class HistoryBean extends DataSupport {
    private String history;

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}

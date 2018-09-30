package com.ityun.zhihuiyun.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class ManageFile {

    private String date;

    private List<ItemFile> itemFiles;

    public String getData() {
        return date;
    }

    public void setData(String date) {
        this.date = date;
    }

    public List<ItemFile> getItemFiles() {
        return itemFiles;
    }

    public void setItemFiles(List<ItemFile> itemFiles) {
        this.itemFiles = itemFiles;
    }
}

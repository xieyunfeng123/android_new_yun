package com.ityun.zhihuiyun.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/8/30 0030.
 */

public class DepartmentBean implements Serializable {

    private int result;

    private int totnum;


    private List<Department> departments;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getTotnum() {
        return totnum;
    }

    public void setTotnum(int totnum) {
        this.totnum = totnum;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}

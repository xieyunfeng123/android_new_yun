package com.ityun.zhihuiyun.bean;

import android.text.TextUtils;

import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.db.RemarkUtil;
import com.ityun.zhihuiyun.home.HomeActivity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class Account implements Serializable {

    private int id;

    private String name;

//    private String account;


    private int departmentid;

    private String departmentname;

    private int role;

    private String tel;

    private int organizeid;

    private String organizename;


    private int enpid;


    private String email;


    private int userauthflag;


    private int status;


    private String sort;


    private boolean choose;


    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    public boolean getChoose() {
        return choose;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public String getRemarkName() {
        SysConfBean bean = App.getInstance().getSysConfBean();
        if (bean != null) {
            RemarkName remarkName = RemarkUtil.getInstance().selectRemark(bean.getAccountid(), id);
            if (remarkName != null && !TextUtils.isEmpty(remarkName.getRemarkName().trim())) {
                return remarkName.getRemarkName();
            }
        }
        return "";
    }


    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getOrganizeid() {
        return organizeid;
    }

    public void setOrganizeid(int organizeid) {
        this.organizeid = organizeid;
    }

    public String getOrganizename() {
        return organizename;
    }

    public void setOrganizename(String organizename) {
        this.organizename = organizename;
    }

    public int getEnpid() {
        return enpid;
    }

    public void setEnpid(int enpid) {
        this.enpid = enpid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserauthflag() {
        return userauthflag;
    }

    public void setUserauthflag(int userauthflag) {
        this.userauthflag = userauthflag;
    }

    public int getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(int departmentid) {
        this.departmentid = departmentid;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }
}

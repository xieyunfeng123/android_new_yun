package com.ityun.zhihuiyun.bean;

import java.io.Serializable;

/**
 * {
 * "accountid" : 100,
 * "httpport"  : 9050,
 * "imsvrhttpport" : 9050,
 * "imsvrid" : 1,
 * "imsvrip" : "192.168.0.88",
 * "imsvrport" : 9000,
 * "signature" : "f899139df5e10593",
 * "thirdparty" : {
 * "swsignature" : "abscdwecsdvfbguyjuyhmnghnfr"
 * },
 * "version" : {
 * "number" : "1.0.0.1",
 * "url" : "http://192.168.0.88/123.apk"
 * },
 * "vfilesvrdownport" : 8080,
 * "vfilesvrhttpport" : 9090,
 * "vfilesvrid" : 1,
 * "vfilesvrip" : "192.168.0.88"
 * }
 */
public class SysConfBean implements Serializable {

    private int accountid;

    private int httpport;

    private int imsvrhttpport;

    private int imsvrid;

    private String imsvrip;

    private int imsvrport;

    private String signature;

    private Thirdparty thirdparty;

    private int vfilesvrdownport;

    private int vfilesvrhttpport;

    private int vfilesvrid;

    private String vfilesvrip;

    public int getAccountid() {
        return accountid;
    }

    public void setAccountid(int accountid) {
        this.accountid = accountid;
    }

    public int getHttpport() {
        return httpport;
    }

    public void setHttpport(int httpport) {
        this.httpport = httpport;
    }

    public int getImsvrhttpport() {
        return imsvrhttpport;
    }

    public void setImsvrhttpport(int imsvrhttpport) {
        this.imsvrhttpport = imsvrhttpport;
    }

    public int getImsvrid() {
        return imsvrid;
    }

    public void setImsvrid(int imsvrid) {
        this.imsvrid = imsvrid;
    }

    public String getImsvrip() {
        return imsvrip;
    }

    public void setImsvrip(String imsvrip) {
        this.imsvrip = imsvrip;
    }

    public int getImsvrport() {
        return imsvrport;
    }

    public void setImsvrport(int imsvrport) {
        this.imsvrport = imsvrport;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Thirdparty getThirdparty() {
        return thirdparty;
    }

    public void setThirdparty(Thirdparty thirdparty) {
        this.thirdparty = thirdparty;
    }

    public int getVfilesvrdownport() {
        return vfilesvrdownport;
    }

    public void setVfilesvrdownport(int vfilesvrdownport) {
        this.vfilesvrdownport = vfilesvrdownport;
    }

    public int getVfilesvrhttpport() {
        return vfilesvrhttpport;
    }

    public void setVfilesvrhttpport(int vfilesvrhttpport) {
        this.vfilesvrhttpport = vfilesvrhttpport;
    }

    public int getVfilesvrid() {
        return vfilesvrid;
    }

    public void setVfilesvrid(int vfilesvrid) {
        this.vfilesvrid = vfilesvrid;
    }

    public String getVfilesvrip() {
        return vfilesvrip;
    }

    public void setVfilesvrip(String vfilesvrip) {
        this.vfilesvrip = vfilesvrip;
    }
//    private int  userid;
//
//    private String address;
//
//    private String signature;
//
//    public int getUserid()
//    {
//        return userid;
//    }
//
//    public void setUserid(int userid)
//    {
//        this.userid = userid;
//    }
//
//    public String getAddress()
//    {
//        return address;
//    }
//
//    public void setAddress(String address)
//    {
//        this.address = address;
//    }
//
//    public String getSignature()
//    {
//        return signature;
//    }
//
//    public void setSignature(String signature)
//    {
//        this.signature = signature;
//    }

}

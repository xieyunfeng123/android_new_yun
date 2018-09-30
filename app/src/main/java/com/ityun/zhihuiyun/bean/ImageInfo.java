package com.ityun.zhihuiyun.bean;

/**
 * Created by Administrator on 2018/6/14 0014.
 * 图片信息
 */

public class ImageInfo {

    /* 名称 */
    private String name;
    /* 路径 */
    private String path;
    /* 最后修改时间 */
    private long lastModified;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}

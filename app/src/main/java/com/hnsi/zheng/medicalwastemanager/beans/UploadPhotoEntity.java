package com.hnsi.zheng.medicalwastemanager.beans;

/**
 * Created by Zheng on 2018/7/17.
 */

public class UploadPhotoEntity {
    /**
     * createBy : 7
     * createTime : 2018-07-17 10:16:59
     * fileExt : jpg
     * fileName : 8d1fdb13-f8a6-4dac-8e98-ccb7f3e296c7.jpg
     * fileNewName : 1019043252709494785.jpg
     * fileSize : 4303.583984375
     * fileUrl : upload/201807198/1019043252709494785.jpg
     * id : 1
     */

    private int createBy;
    private String createTime;
    private String fileExt;
    private String fileName;
    private String fileNewName;
    private double fileSize;
    private String fileUrl;
    private int id;

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileNewName() {
        return fileNewName;
    }

    public void setFileNewName(String fileNewName) {
        this.fileNewName = fileNewName;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UploadPhotoEntity{" +
                "createBy=" + createBy +
                ", createTime='" + createTime + '\'' +
                ", fileExt='" + fileExt + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileNewName='" + fileNewName + '\'' +
                ", fileSize=" + fileSize +
                ", fileUrl='" + fileUrl + '\'' +
                ", id=" + id +
                '}';
    }
}

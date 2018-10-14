package com.hnsi.zheng.medicalwastemanager.beans.format;

import com.hnsi.zheng.medicalwastemanager.exception.InvalidBucketCodeDataException;

/**
 * Author: Create by Zheng on 2018/10/9 23:05
 * E-mail: zhengCH12138@163.com
 *
 * 医废桶二维码数据实体
 */
public class BucketCodeDataEntity {

    private String orgId;//医院ID
    private String orgName;//医院名称
    private String bucketType;//医废桶类型
    private String guid;//医废桶编号

    public BucketCodeDataEntity(){}

    public BucketCodeDataEntity(String formatStr) throws InvalidBucketCodeDataException {
        if (formatStr == null){
            throw new InvalidBucketCodeDataException("医废桶二维码数据字符串无效");
        }
        String[] strArrays = formatStr.split("_");
        if (strArrays.length != 4){
            throw new InvalidBucketCodeDataException("医废桶二维码数据数组无效");
        }
        this.orgId = strArrays[0];
        this.orgName = strArrays[1];
        this.bucketType = strArrays[2];
        this.guid = strArrays[3];
    }

    /**
     * 获取医废桶二维码数据对象的格式化字符串
     * @return
     */
    public String toFormatStr() throws InvalidBucketCodeDataException {
        if (!isValidStr(orgId)){
            throw new InvalidBucketCodeDataException("医废桶二维码数据无效:医院ID无效");
        }
        if (!isValidStr(orgName)){
            throw new InvalidBucketCodeDataException("医废桶二维码数据无效:医院名称无效");
        }
        if (!isValidStr(bucketType)){
            throw new InvalidBucketCodeDataException("医废桶二维码数据无效:医废桶类型无效");
        }
        if (!isValidStr(guid)){
            throw new InvalidBucketCodeDataException("医废桶二维码数据无效:医废桶编号无效");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(orgId).append("_")
                .append(orgName).append("_")
                .append(bucketType).append("_")
                .append(guid);
        return builder.toString();
    }

    private boolean isValidStr(String str){
        str = str.trim();
        if (str == null) return false;
        if ("".equals(str)) return false;
        if ("null".equals(str)) return false;
        return true;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getBucketType() {
        return bucketType;
    }

    public void setBucketType(String bucketType) {
        this.bucketType = bucketType;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "BucketCodeDataEntity{" +
                "orgId='" + orgId + '\'' +
                ", orgName='" + orgName + '\'' +
                ", bucketType='" + bucketType + '\'' +
                ", guid='" + guid + '\'' +
                '}';
    }
}

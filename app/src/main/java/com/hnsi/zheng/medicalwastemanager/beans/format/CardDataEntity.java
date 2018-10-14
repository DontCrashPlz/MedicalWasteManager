package com.hnsi.zheng.medicalwastemanager.beans.format;

import com.hnsi.zheng.medicalwastemanager.exception.InvalidCardDataException;

import java.io.Serializable;

public class CardDataEntity implements Serializable {

//    IC卡数据格式："123456783_7_孙利平_001_第一人民医院_011_后勤科_2"
    private String icCardId;//IC卡编号
    private String userId;//用户ID
    private String userName;//用户姓名
    private String orgId;//医院ID
    private String orgName;//医院名称
    private String departmentId;//部门ID
    private String departmentName;//部门名称
    private String userType;//用户类型

    public CardDataEntity(){}

    public CardDataEntity(String formatStr) throws InvalidCardDataException {
        if (formatStr == null){
            throw new InvalidCardDataException("IC卡数据字符串无效");
        }
        String[] strArrays = formatStr.split("_");
        if (strArrays.length != 8){
            throw new InvalidCardDataException("IC卡数据数组无效");
        }
        this.icCardId = strArrays[0];
        this.userId = strArrays[1];
        this.userName = strArrays[2];
        this.orgId = strArrays[3];
        this.orgName = strArrays[4];
        this.departmentId = strArrays[5];
        this.departmentName = strArrays[6];
        this.userType = strArrays[7];
    }

    /**
     * 获取用户对象的格式化字符串
     * IC卡数据格式："123456783_7_孙利平_001_第一人民医院_011_后勤科_2"
     * @return
     */
    public String toFormatStr() throws InvalidCardDataException {
        if (!isValidStr(icCardId)){
            throw new InvalidCardDataException("IC卡数据无效:IC卡ID无效");
        }
        if (!isValidStr(userId)){
            throw new InvalidCardDataException("IC卡数据无效:用户ID无效");
        }
        if (!isValidStr(userName)){
            throw new InvalidCardDataException("IC卡数据无效:用户名称无效");
        }
        if (!isValidStr(orgId)){
            throw new InvalidCardDataException("IC卡数据无效:医院ID无效");
        }
        if (!isValidStr(orgName)){
            throw new InvalidCardDataException("IC卡数据无效:医院名称无效");
        }
        if (!isValidStr(departmentId)){
            throw new InvalidCardDataException("IC卡数据无效:部门ID无效");
        }
        if (!isValidStr(departmentName)){
            throw new InvalidCardDataException("IC卡数据无效:部门名称无效");
        }
        if (!isValidStr(userType)){
            throw new InvalidCardDataException("IC卡数据无效:用户类型无效");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(icCardId).append("_")
                .append(userId).append("_")
                .append(userName).append("_")
                .append(orgId).append("_")
                .append(orgName).append("_")
                .append(departmentId).append("_")
                .append(departmentName).append("_")
                .append(userType);
        return builder.toString();
    }

    private boolean isValidStr(String str){
        str = str.trim();
        if (str == null) return false;
        if ("".equals(str)) return false;
        if ("null".equals(str)) return false;
        return true;
    }

    public String getIcCardId() {
        return icCardId;
    }

    public void setIcCardId(String icCardId) {
        this.icCardId = icCardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "CardDataEntity{" +
                "icCardId='" + icCardId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", orgId='" + orgId + '\'' +
                ", orgName='" + orgName + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }

}

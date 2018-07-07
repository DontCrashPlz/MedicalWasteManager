package com.hnsi.zheng.medicalwastemanager.utils;

/**
 * Created by Zheng on 2018/7/7.
 */

public class Tools {

    public static String getWasteTypeNameById(int wasteTypeId){
        String wasteTypeName= "";
        switch (wasteTypeId){
            case 1:
                wasteTypeName= "感染性医废";
                break;
            case 2:
                wasteTypeName= "损伤性性医废";
                break;
            case 3:
                wasteTypeName= "药物性医废";
                break;
            case 4:
                wasteTypeName= "病理性医废";
                break;
            case 5:
                wasteTypeName= "化学性医废";
                break;
            default:
                wasteTypeName= "未知类型医废";
                break;
        }
        return wasteTypeName;
    }

    public static int getWasteTypeIdByName(String wasteTypeName){
        int wasteTypeId= 0;
        switch (wasteTypeName){
            case "感染性医废":
                wasteTypeId= 1;
                break;
            case "损伤性性医废":
                wasteTypeId= 2;
                break;
            case "药物性医废":
                wasteTypeId= 3;
                break;
            case "病理性医废":
                wasteTypeId= 4;
                break;
            case "化学性医废":
                wasteTypeId= 5;
                break;
            default:
                wasteTypeId= 0;
                break;
        }
        return wasteTypeId;
    }

    public static float formatWasteWeigh(String weighStr){
        int strSize= weighStr.length();
        LogUtil.d("当前重量截取前：", weighStr);
        String floatStr= weighStr.substring(0, strSize - 4);
        LogUtil.d("当前重量截取后：", floatStr);
        return Float.parseFloat(floatStr);
    }

}

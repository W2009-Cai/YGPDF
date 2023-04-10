package com.yg.ad.utils;

public class PDFAdError {

    private int errorCode;
    private String errorMsg;
    protected String platformCode;
    protected String platformMSG;

    public PDFAdError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.platformCode = platformCode;
        this.platformMSG = platformMSG;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getPlatformMSG() {
        return platformMSG;
    }

    public void setPlatformMSG(String platformMSG) {
        this.platformMSG = platformMSG;
    }

    public String printStackTrace() {
        return "code:[ " + errorCode + " ]" +
                "desc:[ " + errorMsg + " ]" +
                "platformCode:[ " + platformCode + " ]" +
                "platformMSG:[ " + platformMSG + " ]";
    }
}

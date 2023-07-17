package com.example.dangerbehiviordetect.Entity;

public class ChartInfo {
    public String cid;
    public String owner;

    public ChartInfo(CameraInfo cameraInfo) {
        this.cid = String.valueOf(cameraInfo.cid);
        this.owner = String.valueOf(cameraInfo.owner);
        this.content = cameraInfo.content;
    }

    public String content;
    public String sum_num;
    public ChartInfo(){

    }
    public ChartInfo(String cID, String isOwner, String content, String sum_num){
        this.cid = cID;
        this.owner = isOwner;
        this.content = content;
        this.sum_num = sum_num;
    }
}

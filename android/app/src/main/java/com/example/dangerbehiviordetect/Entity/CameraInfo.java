package com.example.dangerbehiviordetect.Entity;

public class CameraInfo {
    public Double cid;
    public Boolean owner;
    public String content;
//    public String url;
    public CameraInfo(){

    }
    public CameraInfo(Double cID, Boolean isOwner, String content){
        this.cid = cID;
        this.owner = isOwner;
        this.content = content;
//        this.url = url;
    }




}

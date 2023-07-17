package com.example.dangerbehiviordetect.Entity;

public class RecordInfo {
    public String pid;//可疑点编号
    public String stime;//开始时间
    public String etime;//结束时间
    public RecordInfo(){

    }

    public RecordInfo(String pid, String stime, String etime){
        this.pid = pid;
        this.stime = stime;
        this.etime = etime;
    }
}

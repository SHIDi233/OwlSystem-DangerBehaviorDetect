package com.example.dangerbehiviordetect;

public class MyCameras {
    public int cID;
    public String addr;
    public String content;

    public MyCameras(int cID, String addr, String content) {
        this.cID = cID;
        this.addr = addr;
        this.content = content;
    }

    @Override
    public String toString() {
        return "MyCameras{" +
                "cID=" + cID +
                ", addr='" + addr + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public int getcID() {
        return cID;
    }

    public void setcID(int cID) {
        this.cID = cID;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

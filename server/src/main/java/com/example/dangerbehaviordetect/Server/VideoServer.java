package com.example.dangerbehaviordetect.Server;

import com.example.dangerbehaviordetect.pojo.Camera_return;
import com.example.dangerbehaviordetect.pojo.JumpInfo;
import com.example.dangerbehaviordetect.pojo.Playback_return;
import com.example.dangerbehaviordetect.pojo.Suspicion;

import java.util.List;
import java.util.Map;

public interface VideoServer {

    public String getVideo(int cID);

    public List<Playback_return> getPlaybacks(int cID, int year, int month, int day);

    public List<Suspicion> getSus(Integer uID, Integer cID, Integer year, Integer month, Integer day);

    public String getPlayback(int pID);

    public JumpInfo jump(int sID);

    public boolean upgrade(String startTime, int fps, int cID, String videoUrl, String xlsUrl);

    public List<Camera_return> getCameras(int uID);

    public int addCamera(int uID, String addr, String content);

    public Map<String, Object> count(Integer uID, Integer cID, String sTime, String cTime);

    public List<Map<String, Object>> statistic(int cID, String time);

    public List<Map<String, Object>> count_android(int cID, String type);

    public void addZone(int cID, String zone);

    public String getZone(int cID);

    public void flush(int cID);

    public boolean needFlush(int cID);

    public void addSus(String sTime, int cID, String type, int cnt);

    public String getAxis(int cID);

    public void setAxis(int cID, String axis);

    public List<Map<String, Object>> getAxises(int uID);
}

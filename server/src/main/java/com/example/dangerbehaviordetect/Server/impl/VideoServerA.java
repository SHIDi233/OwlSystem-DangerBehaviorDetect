package com.example.dangerbehaviordetect.Server.impl;

import com.example.dangerbehaviordetect.Mapper.CameraMapper;
import com.example.dangerbehaviordetect.Mapper.MemberMapper;
import com.example.dangerbehaviordetect.Mapper.PlaybackMapper;
import com.example.dangerbehaviordetect.Mapper.SuspicionMapper;
import com.example.dangerbehaviordetect.Server.VideoServer;
import com.example.dangerbehaviordetect.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VideoServerA implements VideoServer {

    @Autowired
    private CameraMapper cameraMapper;

    @Autowired
    private PlaybackMapper playbackMapper;

    @Autowired
    private SuspicionMapper suspicionMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public String getVideo(int cID) {
        Camera camera = cameraMapper.getByID(cID);
        if(camera == null) { return null; }
        return camera.getAddr();
    }

    @Override
    public List<Playback_return> getPlaybacks(int cID, int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        List<Playback> playbackList = playbackMapper.getByDate(cID, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        List<Playback_return> playbacks = new ArrayList<>();

        //将获得的数据转化为指定格式
        for(Playback playback : playbackList) {
            LocalDateTime sTime = playback.getStartTime();
            //计算结束时间
            LocalDateTime eTime = sTime.plusSeconds(playback.getFps() / 30);
            Playback_return pkr = new Playback_return(playback.getPID(), sTime, eTime);
            playbacks.add(pkr);
        }

        return playbacks;
    }

    @Override
    public List<Suspicion> getSus(Integer uID, Integer cID, Integer year, Integer month, Integer day) {
        return suspicionMapper.getSus(uID, cID, year, month, day);
    }

    @Override
    public String getPlayback(int pID) {
        return playbackMapper.getByID(pID).getVideoUrl();
    }

    @Override
    public JumpInfo jump(int sID) {
        List<Playback> playbacks = playbackMapper.getBySus(sID);
        if(playbacks.size() == 0) { return null; }
        Suspicion suspicion = suspicionMapper.getByID(sID);
        Duration duration = Duration.between(playbacks.get(0).getStartTime(), suspicion.getSTime());
        int j = (int) (duration.toMillis()/1000);
        if(j > 2) {
            j -= 2;
        }
        return new JumpInfo(playbacks.get(0).getVideoUrl(), j);
    }

    @Override
    public boolean upgrade(String startTime, int fps, int cID, String videoUrl, String xlsUrl) {
        playbackMapper.upgrade(startTime, fps, cID, videoUrl, xlsUrl);
        return true;
    }

    @Override
    public List<Camera_return> getCameras(int uID) {
        return memberMapper.getCameras(uID);
    }

    @Override
    public int addCamera(int uID, String addr, String content) {
        Camera camera = new Camera();
        camera.setAddr(addr);
        camera.setContent(content);
        camera.setOwnerID(uID);
        cameraMapper.addCamera(camera);
        memberMapper.addOwner(uID, camera.getCID());
        return camera.getCID();
    }

    @Override
    public Map<String, Object> count(Integer uID, Integer cID, String sTime, String cTime) {
        List<Suspicion> suspicions;
        if(cID != null) {
            suspicions = suspicionMapper.getCnt(cID, sTime, cTime);
        } else {
            suspicions = suspicionMapper.getCntAll(uID, sTime, cTime);
        }
//        System.out.println(suspicions);
//        List<Map<String, Object>> cnts = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<Integer> c = new ArrayList<>();
        for(Suspicion sus : suspicions) {
            types.add(sus.getType());
            c.add(sus.getCnt());
        }
        Map<String, Object> cnt = new HashMap<>();
        cnt.put("type", types);
        cnt.put("cnt", c);
        return cnt;
    }

    @Override
    public List<Map<String, Object>> statistic(int cID, String time) {
        List<Suspicion> suspicions = suspicionMapper.getByCID(cID);
        List<Map<String, Object>> res = new ArrayList<>();
        LocalDateTime t = LocalDateTime.now();
        if(time.equals("week")) {
            t = t.minusDays(7);
            t = LocalDateTime.of(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), 0, 0, 0);
            int cnt = 0;
            for(Suspicion sus : suspicions) {
                if(sus.getSTime().compareTo(t) > 0 && sus.getSTime().compareTo(t.plusDays(1)) < 0) {
                    cnt += sus.getCnt();
                } else {
                    Map<String, Object> m = new HashMap<>();
                    m.put("date", t.getMonthValue() + "." + t.getDayOfMonth());
                    m.put("cnt", cnt);
                    res.add(m);
                    cnt = 0;
                }
            }
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> count_android(int cID, String type) {
        List<Suspicion> suspicions = null;
        if(type.equals("week")) {
            suspicions = suspicionMapper.getCnt_week(cID);
        } else if (type.equals("day")) {
            suspicions = suspicionMapper.getCnt_day(cID);
        } else if (type.equals("hour")) {
            suspicions = suspicionMapper.getCnt_hour(cID);
        }
        if (suspicions == null) { return null; }
        List<Map<String, Object>> cnts = new ArrayList<>();
        for(Suspicion sus : suspicions) {
            Map<String, Object> cnt = new HashMap<>();
            cnt.put("type", sus.getType());
            cnt.put("cnt", sus.getCnt());
            cnts.add(cnt);
        }
        boolean call = false;
        boolean smoke = false;
        boolean down = false;
        for(Map<String, Object> c : cnts) {
            if(c.get("type").equals("smoking")) {
                smoke = true;
            } else if (c.get("type").equals("calling")) {
                call = true;
            } else if (c.get("type").equals("down")) {
                down = true;
            }
        }
        if(!call) {
            Map<String, Object> cnt = new HashMap<>();
            cnt.put("type", "calling");
            cnt.put("cnt", 0);
            cnts.add(cnt);
        }
        if(!smoke) {
            Map<String, Object> cnt = new HashMap<>();
            cnt.put("type", "smoking");
            cnt.put("cnt", 0);
            cnts.add(cnt);
        }
        if(!down) {
            Map<String, Object> cnt = new HashMap<>();
            cnt.put("type", "down");
            cnt.put("cnt", 0);
            cnts.add(cnt);
        }
        List<Map<String, Object>> res = new ArrayList<>();
        for(Map<String, Object> c : cnts) {
            if(c.get("type").equals("calling")) {
                res.add(c);
                break;
            }
        }
        for(Map<String, Object> c : cnts) {
            if(c.get("type").equals("smoking")) {
                res.add(c);
                break;
            }
        }
        for(Map<String, Object> c : cnts) {
            if(c.get("type").equals("down")) {
                res.add(c);
                break;
            }
        }
        return res;
    }

    @Override
    public void addZone(int cID, String zone) {
        Zone zone1 = suspicionMapper.getZone(cID);
        if(zone1 != null) {
            suspicionMapper.updateZone(cID, zone);
        } else {
            suspicionMapper.addZone(cID, zone);
        }
    }

    @Override
    public String getZone(int cID) {
        Zone zone = suspicionMapper.getZone(cID);
        if(zone == null) {
            return "null";
        }
        return zone.getAxis();
    }

    @Override
    public void flush(int cID) {
        cameraMapper.flush(cID, 1);
    }

    @Override
    public boolean needFlush(int cID) {
        Camera camera = cameraMapper.getByID(cID);
//        System.out.println(camera.getCID() + ", " + camera.getFlush());
        if(camera.getFlush() == 1) {
            cameraMapper.flush(cID, 0);
            return true;
        }
        return false;
    }

    @Override
    public void addSus(String sTime, int cID, String type, int cnt) {

        suspicionMapper.addSus(sTime, cID, type, cnt);
    }

    @Override
    public String getAxis(int cID) {
        return cameraMapper.getAxis(cID);
    }

    @Override
    public void setAxis(int cID, String axis) {
        cameraMapper.setAxis(cID, axis);
    }

    @Override
    public List<Map<String, Object>> getAxises(int uID) {
        List<Map<String, Object>> res = new ArrayList<>();
        List<Camera> cameras = cameraMapper.getAxises(uID);
        for(Camera camera : cameras) {
            Map<String, Object> m = new HashMap<>();
            m.put("cID", camera.getCID());
            m.put("axis", camera.getAxis());
            res.add(m);
        }
        return res;
    }
}

package com.example.dangerbehaviordetect.Controller;

import com.example.dangerbehaviordetect.Mapper.CameraMapper;
import com.example.dangerbehaviordetect.Mapper.UserMapper;
import com.example.dangerbehaviordetect.Server.VideoServer;
import com.example.dangerbehaviordetect.pojo.Playback_return;
import com.example.dangerbehaviordetect.pojo.Result;
import com.example.dangerbehaviordetect.pojo.Suspicion;
import com.example.dangerbehaviordetect.pojo.VideoTool;
import com.example.dangerbehaviordetect.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class VideoController {

    @Autowired
    private VideoServer videoServer;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private CameraMapper cameraMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/upgrade")
    public Result upgrade(String startTime, int fps, int cID, String videoUrl, String xlsUrl) {
        log.info("上传回放信息中：startTime：" + startTime + ", " + fps + ", " + cID);
        videoServer.upgrade(startTime, fps, cID, videoUrl, xlsUrl);
        log.info("上传成功");
        return Result.success();
    }

    @GetMapping("/addSus")
    public  Result addSus() {
        return null;
    }

    @GetMapping("/record")
    public Result getVideo(int cID) {
        log.info("正在获取摄像头直播，cID：" + cID);
        String url = videoServer.getVideo(cID);
        if(url == null) { return Result.error("未找到摄像头"); }
        return Result.success(url);
    }

    @GetMapping("/playbacks")
    public Result getPlaybacks(int cID, int year, int month, int day) {
        log.info("获取回访列表中：cID:" + cID + ", " + year + ", " + month + ", " + day);
        List<Playback_return> playbacks = videoServer.getPlaybacks(cID, year, month, day);
        return Result.success(playbacks);
    }

    @GetMapping("/sus")
    public Result getSus(HttpServletRequest request) {
//    public Result getSus(int cID, Integer year, Integer month, Integer day) {
        Integer uID;
        String jwt = request.getHeader("token");
        Claims claims = JwtUtils.parseJWT(jwt);
        uID = (Integer) claims.get("uID");
        log.info("用户尝试获取可疑点列表，uID为: " + uID);
        Integer cID = Integer.valueOf(request.getParameter("cID"));
        Integer year = Integer.valueOf(request.getParameter("year"));
        Integer month = Integer.valueOf(request.getParameter("month"));
        Integer day = Integer.valueOf(request.getParameter("day"));
        log.info(cID + ", " + year + ", " + month + ", " + day);
        return Result.success(videoServer.getSus(uID, cID, year, month, day));
    }

    @GetMapping("/sus2")
    public Result getSus_2(HttpServletRequest request) {
        Integer uID;
        String jwt = request.getHeader("token");
        Claims claims = JwtUtils.parseJWT(jwt);
        uID = (Integer) claims.get("uID");
        log.info("用户尝试获取可疑点列表，uID为: " + uID);
        Integer cID = (Integer) claims.get("cID");
        Integer year = (Integer) claims.get("year");
        Integer month = (Integer) claims.get("month");
        Integer day = (Integer) claims.get("day");
        List<Suspicion> sus = videoServer.getSus(uID, cID, year, month, day);
        Map<Integer, Map<String, Object>> res = new HashMap<>();
        for(Suspicion s :sus) {
            Map<Integer, Map<String, Object>> M = new HashMap<>();
            Map<String, Object> m = new HashMap<>();
            m.put("sID", s.getSID());
            m.put("type", s.getType());
            Integer t = s.getSTime().getHour() * 3600 + s.getSTime().getMinute() * 60 + s.getSTime().getSecond();
            res.put(t, m);
        }
        return Result.success(res);
    }

    @GetMapping("/sus3")
    public Result getSus_3(String sTime, String cTime, int cID, String type, int cnt) throws UnsupportedEncodingException, AddressException {
        log.info(sTime + ", " + cTime + ", " + cID + ", " + type + ", " + cnt);
        videoServer.addSus(sTime, cID, type, cnt);

        String mail = userMapper.getByID(cameraMapper.getByID(cID).getOwnerID()).getMail();

        // 邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("危险行为监测系统报警");
        message.setText("尊敬的用户您好!\n\n感谢您使用危险行为监测系统。摄像头:" + cID + "发出报警:" + type + "请及时查看");
        message.setTo(mail);
        // 对方看到的发送人
        message.setFrom(new InternetAddress(MimeUtility.encodeText("软件学院抽象工作室")+"															<2982437139@qq.com>").toString());
        //发送邮件
        try {
            javaMailSender.send(message);
            //valueOperations.set(key,code,5L, TimeUnit.MINUTES);
//            vCodeMapper.insert(mail, code);
            log.info("邮件发送成功");
        }catch (Exception e){
            log.error("邮件发送出现异常");
            log.error("异常信息为"+e.getMessage());
            log.error("异常堆栈信息为-->");
            e.printStackTrace();
            return Result.error("邮件发送失败，请重试");
        }

        return Result.success();
    }

    @GetMapping("/getPlayback")
    public Result getPlayback(int pID) {
        log.info("获取回访，pID：" + pID);
        return Result.success(videoServer.getPlayback(pID));
    }

    @GetMapping("/jumpPlayback")
    public Result jump(int sID) {
        log.info("跳转可疑点中，sID：" + sID);
        return Result.success(videoServer.jump(sID));
    }

    @GetMapping("/cameras")
    public Result getCameras(HttpServletRequest request) {
        int uID;
        String jwt = request.getHeader("token");
        Claims claims = JwtUtils.parseJWT(jwt);
        uID = (int) claims.get("uID");
        log.info("用户尝试获取摄像头列表，uID为: " + uID);
        return Result.success(videoServer.getCameras(uID));
    }

    @PostMapping("/addCamera")
    public Result addCamera(HttpServletRequest request) {
        int uID;
        String jwt = request.getHeader("token");
//        log.info(jwt);
        Claims claims = JwtUtils.parseJWT(jwt);
        uID = (int) claims.get("uID");

        String addr = request.getParameter("addr");
        String content = request.getParameter("content");

        return Result.success(videoServer.addCamera(uID, addr, content));
    }

    @GetMapping("/getImg")
    public Result getImg(int cID) {
        VideoTool videoTool = new VideoTool();
        try {
            videoTool.getThumb("rtmp://116.204.11.171/" + cID + "p/test", cID + ".png", 1280, 720, 0, 0, 0);
        } catch (IOException | InterruptedException e) {
            return Result.error("出现问题，请重新尝试");
        }
        return Result.success("116.204.11.171:8080/images/" + cID + ".png");
    }

    @PostMapping("/addZone")
    public Result addZone(int cID, String zone) {
        log.info("添加区域：" + cID + ", " + zone);
        videoServer.addZone(cID, zone);
        return Result.success();
    }

    @GetMapping("/getZone")
    public Result getZone(int cID) {
        return Result.success(videoServer.getZone(cID));
    }

    @PostMapping("/stitchFlush")
    public Result flush(int cID) {
        log.info("刷新" + cID);
        videoServer.flush(cID);
        log.info("刷新成功");
        return Result.success();
    }

    @GetMapping("needFlush")
    public Result needFlush(int cID) {
        if(videoServer.needFlush(cID)) {
            log.info("已刷新");
            return Result.success("yes");
        }
        return Result.success("no");
    }

    @GetMapping("/getAxis")
    public Result getAxis(int cID) {
        String res = videoServer.getAxis(cID);
        return Result.success(res);
    }

    @PostMapping("/setAxis")
    public Result setAxis(int cID, String axis) {
        log.info("设置坐标：" + cID + ", " + axis);
        videoServer.setAxis(cID, axis);
        return Result.success();
    }

    @GetMapping("/getAxises")
    public Result getAxises(HttpServletRequest request) {
        int uID;
        String jwt = request.getHeader("token");
//        log.info(jwt);
        Claims claims = JwtUtils.parseJWT(jwt);
        uID = (int) claims.get("uID");
        return Result.success(videoServer.getAxises(uID));
    }
}

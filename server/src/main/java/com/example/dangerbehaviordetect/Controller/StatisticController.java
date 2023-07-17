package com.example.dangerbehaviordetect.Controller;

import com.example.dangerbehaviordetect.Server.VideoServer;
import com.example.dangerbehaviordetect.pojo.Result;
import com.example.dangerbehaviordetect.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@CrossOrigin
public class StatisticController {

    @Autowired
    private VideoServer videoServer;

    @GetMapping("/count")
    public Result count(HttpServletRequest request) {
//    public Result count(int cID, String sTime, String eTime) {
        int uID;
        String jwt = request.getHeader("token");
        Claims claims = JwtUtils.parseJWT(jwt);
        uID = (int) claims.get("uID");
        String sTime = request.getParameter("sTime");
        String eTime = request.getParameter("eTime");
        String cIDa = request.getParameter("cID");
        Integer cID = null;
        if(cIDa != null) {
            cID = Integer.valueOf(cIDa);
        }
        log.info("用户查看时间范围：" + sTime + "-" + eTime + "内的统计");
        System.out.println(videoServer.count(uID, cID, sTime, eTime));
        return Result.success(videoServer.count(uID, cID, sTime, eTime));
    }

    @GetMapping("/stat")
    public Result statistic(int cID, String time) {
        return null;
    }

    @GetMapping("/count_android")
    public Result count_android(int cID, String type) {
        log.info("安卓获取统计数据：" + cID + ", " + type);
        return Result.success(videoServer.count_android(cID, type));
    }

}

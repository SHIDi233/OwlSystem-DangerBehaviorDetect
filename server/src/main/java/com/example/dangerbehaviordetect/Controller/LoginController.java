package com.example.dangerbehaviordetect.Controller;

import com.example.dangerbehaviordetect.Mapper.VCodeMapper;
import com.example.dangerbehaviordetect.Server.LoginServer;
import com.example.dangerbehaviordetect.pojo.Result;
import com.example.dangerbehaviordetect.pojo.User;
import com.example.dangerbehaviordetect.utils.JwtUtils;
import com.example.dangerbehaviordetect.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private LoginServer loginServer;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private VCodeMapper vCodeMapper;

    @PostMapping("/login")
    public Result login(String mail, String password) {
        log.info("用户尝试登录：" + mail + ", " + password);
        User user = loginServer.login(mail, password);
        if(user != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("mail", mail);
            claims.put("uID", user.getUID());
            String jwt = JwtUtils.generateJwt(claims);
            log.info("用户:" + user.getMail() + "，ID:" + user.getUID() + "登录成功");
            return Result.success(jwt);
        }
        log.info("用户尝试登录失败，邮箱为:" + mail);
        return Result.error("login fail");
    }

    @PostMapping("/code")
    public Result getCode(String mail) throws UnsupportedEncodingException, AddressException {
        log.info("用户尝试获取验证码: " + mail);
        //随机生成6位验证码
        String code = ValidateCodeUtils.generateValidateCode4String(6);
        // 邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("危险行为监测系统邮箱验证码");
        message.setText("尊敬的用户您好!\n\n感谢您使用危险行为监测系统。\n\n尊敬的: "+mail+"您的校验验证码为: "+code+", 有效期10分钟，请不要把验证码信息泄露给其他人,如非本人请勿操作");
        message.setTo(mail);
        // 对方看到的发送人
        message.setFrom(new InternetAddress(MimeUtility.encodeText("软件学院抽象工作室")+"															<2982437139@qq.com>").toString());
        //发送邮件
        try {
            javaMailSender.send(message);
            //valueOperations.set(key,code,5L, TimeUnit.MINUTES);
            vCodeMapper.insert(mail, code);
            log.info("邮件发送成功");
        }catch (Exception e){
            log.error("邮件发送出现异常");
            log.error("异常信息为"+e.getMessage());
            log.error("异常堆栈信息为-->");
            e.printStackTrace();
            return Result.error("邮件发送失败，请重试");
        }
        return Result.success("验证码已发送");

    }

    @PostMapping("/register")
    public Result register(String mail, String username, String code, String password) {
        log.info("用户尝试注册：" + mail + ", " + password + ", " + username);
        int res = loginServer.register(mail, password, username, code);
        if(res == -2) { return Result.error("邮箱已被注册"); }
        if(res == -1) { return Result.error("验证码错误"); }
        Map<String, Object> claims = new HashMap<>();
        claims.put("mail", mail);
        claims.put("uID", res);
        String jwt = JwtUtils.generateJwt(claims);
        log.info("用户:" + mail + "，ID:" + res + "自动登录成功");
        return Result.success(jwt);
    }

}

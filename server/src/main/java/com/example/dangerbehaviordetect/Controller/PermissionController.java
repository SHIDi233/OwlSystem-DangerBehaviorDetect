package com.example.dangerbehaviordetect.Controller;

import com.example.dangerbehaviordetect.Server.PermissionServer;
import com.example.dangerbehaviordetect.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin
public class PermissionController {

    @Autowired
    private PermissionServer permissionServer;

    @PostMapping("/addMember")
    public Result addMember(Integer cID, String mail) {
        log.info("添加成员中: cID: " + cID + ", mail: " + mail);
        int res = permissionServer.addMember(cID, mail);
        if(res == -2) { return Result.error("被邀请人不存在"); }
        if(res == -1) { return Result.error("被邀请人是管理员"); }
        if(res == 0) { return Result.error("被邀请人已被邀请"); }
        return Result.success("邀请成功");
    }

    @DeleteMapping("/delMember")
    public Result delMember(int cID, int uID) {
        log.info("删除成员cID: " + cID + ", " + uID);
        int res = permissionServer.delMember(cID, uID);
        return Result.success("删除成功");
    }

    @GetMapping("/members")
    public Result getMembers(int cID) {
        return Result.success(permissionServer.getMembers(cID));
    }

}

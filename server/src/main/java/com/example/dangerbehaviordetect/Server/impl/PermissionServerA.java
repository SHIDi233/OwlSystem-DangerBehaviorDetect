package com.example.dangerbehaviordetect.Server.impl;

import com.example.dangerbehaviordetect.Mapper.CameraMapper;
import com.example.dangerbehaviordetect.Mapper.MemberInfo;
import com.example.dangerbehaviordetect.Mapper.MemberMapper;
import com.example.dangerbehaviordetect.Mapper.UserMapper;
import com.example.dangerbehaviordetect.Server.PermissionServer;
import com.example.dangerbehaviordetect.pojo.Camera;
import com.example.dangerbehaviordetect.pojo.Member;
import com.example.dangerbehaviordetect.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionServerA implements PermissionServer {

    @Autowired
    private CameraMapper cameraMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 增加可访问摄像头内容的成员
     * @param cID
     * @param mail
     * @return:
     *     -2:被邀请人不存在
     *     -1:被邀请人是管理员
     *     0:被邀请人已在小组中
     *     1:添加成功
     */
    @Override
    public int addMember(int cID, String mail) {
        Camera camera = cameraMapper.getByID(cID);
        User user = userMapper.getByMail(mail);

        //是否存在
        if(user == null) { return -2; }

        //是否是管理员
        if(camera.getOwnerID() == user.getUID()) { return -1; }

        //是否已经包含
        List<Member> members = memberMapper.getMembers(cID);
        for(Member m : members) {
            if(m.getMemberID() == user.getUID()) { return 0; }
        }

        //添加成员
        memberMapper.addMember(cID, user.getUID());
        return 1;
    }

    @Override
    public int delMember(int cID, int uID) {
        memberMapper.delMember(cID, uID);
        return 0;
    }

    @Override
    public List<MemberInfo> getMembers(int cID) {
        return memberMapper.getMembersByCID(cID);
    }
}

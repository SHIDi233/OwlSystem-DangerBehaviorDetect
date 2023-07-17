package com.example.dangerbehaviordetect.Server;

import com.example.dangerbehaviordetect.Mapper.MemberInfo;

import java.util.List;

public interface PermissionServer {

    public int addMember(int cID, String mail);

    public int delMember(int cID, int uID);

    public List<MemberInfo> getMembers(int cID);

}

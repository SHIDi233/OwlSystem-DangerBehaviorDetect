package com.example.dangerbehaviordetect.Mapper;

import com.example.dangerbehaviordetect.pojo.Camera_return;
import com.example.dangerbehaviordetect.pojo.Member;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberMapper {

    @Select("select * from Member where cID=#{cID} and isOwner=false")
    public List<Member> getMembers(int cID);

    @Insert("insert into Member(memberID, cID) VALUES(#{memberID}, #{cID})")
    public void addMember(int cID, int memberID);

    @Delete("delete from Member where cID=#{cID} and memberID=#{uID}")
    public void delMember(int cID, int uID);

    @Select("select uID, mail, uName, isOwner from Member m, User u where cID=#{cID} and memberID=uID")
    public List<MemberInfo> getMembersByCID(int cID);

    @Select("select c.cID as cID, addr, content, isOwner from Member m, Camera c where m.cID=c.cID and memberID=#{uID}")
    public List<Camera_return> getCameras(int uID);

    @Insert("insert into Member values(#{uID}, #{cID}, true)")
    public void addOwner(int uID, int cID);
}

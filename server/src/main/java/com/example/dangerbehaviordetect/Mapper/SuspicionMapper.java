package com.example.dangerbehaviordetect.Mapper;

import com.example.dangerbehaviordetect.pojo.Suspicion;
import com.example.dangerbehaviordetect.pojo.Zone;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface SuspicionMapper {

    public List<Suspicion> getSus(Integer uID, Integer cID, Integer year, Integer month, Integer day);

    @Select("select * from Suspicion where sID = #{sID}")
    public Suspicion getByID(int sID);

    @Select("select type, count(cnt) from Suspicion where cID=#{cID} and str_to_date(#{sTime}, '%Y-%m-%dT%H:%i:%s') <= sTime " +
            "and sTime <= str_to_date(#{cTime}, '%Y-%m-%dT%H:%i:%s') group by type")
    @Result(column="count(cnt)", property="cnt", jdbcType=JdbcType.INTEGER)
    public List<Suspicion> getCnt(int cID, String sTime, String cTime);

    @Select("select * from Suspicion where cID=#{cID}")
    public List<Suspicion> getByCID(int cID);

    @Select("select type, count(cnt) from Suspicion where cID=#{cID} and sTime >= date_sub(current_time, interval 7 day) group by type")
    @Result(column="count(cnt)", property="cnt", jdbcType=JdbcType.INTEGER)
    public List<Suspicion> getCnt_week(int cID);

    @Select("select type, count(cnt) from Suspicion where cID=#{cID} and sTime >= date_sub(current_time, interval 1 day) group by type")
    @Result(column="count(cnt)", property="cnt", jdbcType=JdbcType.INTEGER)
    public List<Suspicion> getCnt_day(int cID);

    @Select("select type, count(cnt) from Suspicion where cID=#{cID} and sTime >= date_sub(current_time, interval 1 hour) group by type")
    @Result(column="count(cnt)", property="cnt", jdbcType=JdbcType.INTEGER)
    public List<Suspicion> getCnt_hour(int cID);

    @Select("select * from Zone where cID=#{cID}")
    public Zone getZone(int cID);

    @Insert("insert into Zone(cID, axis) values(#{cID}, #{zone})")
    public void addZone(int cID, String zone);

    @Update("update Zone set axis=#{zone} where cID=#{cID}")
    public void updateZone(int cID, String zone);

    @Insert("insert into Suspicion(sTime, cID, type, cnt) values(str_to_date(#{sTime}, '%Y-%m-%d,%H:%i:%s'), #{cID}, #{type}, #{cnt})")
    public void addSus(String sTime, int cID, String type, int cnt);

    @Select("select type, count(cnt) from Suspicion where cID in (select cID from Member where memberID=#{uID}) and str_to_date(#{sTime}, '%Y-%m-%dT%H:%i:%s') <= sTime " +
            "and sTime <= str_to_date(#{cTime}, '%Y-%m-%dT%H:%i:%s') group by type")
    @Result(column="count(cnt)", property="cnt", jdbcType=JdbcType.INTEGER)
    public List<Suspicion> getCntAll(Integer uID, String sTime, String cTime);
}

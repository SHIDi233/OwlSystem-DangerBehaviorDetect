package com.example.dangerbehaviordetect.Mapper;

import com.example.dangerbehaviordetect.pojo.Playback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlaybackMapper {

    @Select("select * from Playback where cID=#{cID} and date_format(startTIme, '%Y-%m-%d')=#{date}")
    public List<Playback> getByDate(int cID, String date);

    @Select("select * from Playback where pID=#{pID}")
    public Playback getByID(int pID);

    @Select("select * from Playback where cID = (select cID from Suspicion where sID=#{sID}) and (select sTime from Suspicion where sID = #{sID}) between startTIme and endTime")
    public List<Playback> getBySus(int sID);

    @Insert("insert into Playback(startTIme, fps, cID, videoUrl, xlsUrl) VALUES(str_to_date(#{startTime}, '%Y-%m-%d-%H-%i-%s'), #{fps}, #{cID}, #{videoUrl}, #{xlsUrl}) ")
    public void upgrade(String startTime, int fps, int cID, String videoUrl, String xlsUrl);

}

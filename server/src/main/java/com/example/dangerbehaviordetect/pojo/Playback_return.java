package com.example.dangerbehaviordetect.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playback_return {

    private int pID;
    private LocalDateTime sTime;
    private LocalDateTime eTime;

}

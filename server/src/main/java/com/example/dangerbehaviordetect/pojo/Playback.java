package com.example.dangerbehaviordetect.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playback {

    private int pID;
    private LocalDateTime startTime;
    private int fps;
    private int cID;
    private String videoUrl;
    private String xlsUrl;
    private LocalDateTime endTime;

}

package com.example.dangerbehaviordetect.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suspicion {

    private int sID;
    private int cID;
    private LocalDateTime sTime;
    private String type;
    private int cnt;

}

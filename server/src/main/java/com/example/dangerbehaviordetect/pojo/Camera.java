package com.example.dangerbehaviordetect.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera {

    private int cID;
    private String addr;
    private String content;
    private int ownerID;
    private int flush;
    private String axis;

}

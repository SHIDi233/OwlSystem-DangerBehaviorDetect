package com.example.dangerbehaviordetect.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private int cID;
    private int memberID;
    private boolean isOwner;

}

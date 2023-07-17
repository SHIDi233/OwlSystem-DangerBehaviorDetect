package com.example.dangerbehaviordetect.Mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfo {

    private int uID;
    private String mail;
    private String uName;
    private boolean isOwner;

}

package com.example.dangerbehaviordetect.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int uID;
    private String mail;
    private String password;
    private String uName;
    private String head;

}

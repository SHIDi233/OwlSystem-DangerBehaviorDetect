package com.example.dangerbehaviordetect.Server;

import com.example.dangerbehaviordetect.pojo.User;

import java.util.List;

public interface LoginServer {
    public User login(String mail, String password);

    public int register(String mail, String password, String uName, String code);
}

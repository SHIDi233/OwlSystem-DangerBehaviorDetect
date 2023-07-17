//package com.example.dangerbehaviordetect.Configure;
//
//import com.example.dangerbehaviordetect.pojo.LogParamsInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Component
//public class WebConfig implements  WebMvcConfigurer {
//
//
//    @Autowired
//    LogParamsInterceptor timeInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(timeInterceptor);
//    }
//}
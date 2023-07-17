package com.example.dangerbehaviordetect.Configure;//package com.example.dangerbehaviordetect.Configure;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMVCConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        //进行跨域配置
//        //前端占用8080 后端占用8888
//        //两个端口之间的访问就是跨域
//        //要允许8080端口访问8888
//        registry.addMapping("/**").allowedOrigins("http://172.30.93.99:8080/").allowedOrigins("http://172.27.136.223:8080").allowedOrigins("http://172.26.160.238:8080");
////        registry.addMapping("/**").allowedOrigins("http://192.168.43.83:8080/");
//    }
//}

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 实现 WebMvcConfigurer(全局跨域) 重写 addCorsMappings 方法设置跨域映射
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/6/6 8:08
 */
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //CorsRegistration addMapping(String pathPattern): 添加路径映射，如 /admin/info，或者 /admin/**
        registry.addMapping("/**")
                //是否发送Cookie
                .allowCredentials(true)
                //放行哪些原始域, * 表示所有
                .allowedOrigins(new String[]{"http://172.30.93.99:8080", "http://172.27.136.223:8080", "http://172.26.160.238:8080", "http://192.168.10.51:8080", "http://0.0.0.0:8080", "http://116.204.11.171:8081", "http://192.168.10.165:8080"})
                //放行哪些请求方式
                .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE"})
                //放行哪些原始请求头部信息
                .allowedHeaders("*");
        //暴露哪些头部信息，不能设置为 * : .exposedHeaders();
    }

    @Configuration
    public class WebMvcConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            //  /home/file/**为前端URL访问路径  后面 file:xxxx为本地磁盘映射
            registry.addResourceHandler("/images/**").addResourceLocations("file:D://images/");
        }
    }
}
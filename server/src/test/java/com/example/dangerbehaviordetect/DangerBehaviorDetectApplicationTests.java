package com.example.dangerbehaviordetect;

import com.example.dangerbehaviordetect.pojo.VideoTool;
import com.example.dangerbehaviordetect.utils.ValidateCodeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DangerBehaviorDetectApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void vCodeTest() {
        for(int i = 0; i < 5; i++) {
            String s = ValidateCodeUtils.generateValidateCode4String(6);
            System.out.println(s);
        }
    }

    @Test
    void videoTest() throws IOException, InterruptedException {
        VideoTool v = new VideoTool();
        v.getThumb("rtmp://116.204.11.171/2p/test", "ffmpeg3.png", 204, 140, 0, 0, 0);
    }

}

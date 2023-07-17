import subprocess
import threading
import time
import requests
import cv2
import imutils
import numpy as np
from PIL import ImageChops

import SIFT.Sift2 as ss

from stitching.SIFT.Panorama import Stitcher

# global img1
# global img2
global bias

# global bol
bol = 'yes'
bias=0
bias_max = 60
list1=[]
list2=[]

ffmpeg_cmd = ['ffmpeg',
                  '-re',
                  '-r', '23',
                  '-y',  # 覆盖已存在的文件
                  '-f', 'rawvideo',
                  '-pixel_format', 'bgr24',
                  '-video_size', '1700x478',
                  '-i', '-',  # 从标准输入读取数据
                  '-vcodec', 'libx264',  # 使用x264编码器
                  '-tune', 'zerolatency',
                  '-pix_fmt', 'yuv420p',
                  '-f', 'flv',
                  'rtmp://127.0.0.1:1935/11p/test']
ffmepg_process = subprocess.Popen(ffmpeg_cmd, stdin=subprocess.PIPE)

def refresh():
    global  bol
    while(True):
        params = {'cID': 11}
        response = requests.get('http://116.204.11.171:8080/needFlush', params=params)
        bol = response.json()['data']
        print(bol)
        time.sleep(1)
    pass

def rec():
    global bol
    global bias
    i = 0
    stitcher = Stitcher()
    res = None
    while (True):
        if (len(list1) > bias_max - 15 and len(list2) > bias_max - 15):
            i += 1
            if i==1:
                if bias >= 0:
                    res, stitched = stitcher.stitch([list1[0 + bias], list2[0]], 1, showMatches=True)
                else:
                    res, stitched = stitcher.stitch([list1[0], list2[0 - bias]], 1, showMatches=True)
                bol="no"
            if bol=="yes":
                if bias >= 0:
                    res, stitched = stitcher.stitch([list1[0 + bias], list2[0]], 1, showMatches=True)
                else:
                    res, stitched = stitcher.stitch([list1[0], list2[0 - bias]], 1, showMatches=True)
                bol="no"
            else:
                if bias >= 0:
                    res, stitched = stitcher.stitch([list1[0 + bias], list2[0]], 0, showMatches=True)
                else:
                    res, stitched = stitcher.stitch([list1[0], list2[0 - bias]], 0, showMatches=True)
            res=cv2.resize(res,(1700,478))
            ffmepg_process.stdin.write(res)
            # print(res.shape)
            # cv2.imshow("result", res)
            # cv2.waitKey(1)

# 打开本地视频文件或网络摄像头，并播放视频
def OpenAndPlayVideoFile(filename, num):
    global bias
    try:
        print("======to start play video file...")
        # 使用OpenCV自带的VideoCpture()函数定义视频文件对象，网络摄像机本质上可以看作远程网络视频文件
        cap = cv2.VideoCapture(filename)

        # 循环读取每一帧
        while (cap.isOpened()):
            # 第一个返回值result是一个布尔值，表示当前这一帧是否获取正确
            (result, frame) = cap.read()
            frame = imutils.resize(frame, width=850)
            # 读取视频文件结束时，退出播放
            if not result:
                print('play end...')
            # cv2.imshow(filename, frame)
            # cv2.waitKey(1)
            if num==1:
                list1.append(frame)
                if len(list1) > bias_max:
                    list1.pop(0)
                # img1 = frame
            else:
                list2.append(frame)
                if len(list2) > bias_max:
                    list2.pop(0)
                # img2 = frame

        # 释放视频文件或摄像头资源
        cap.release()
        # 销毁所有窗口，释放资源
        cv2.destroyAllWindows()
        print("end...")
    except Exception as e:
        # 访问异常的错误编号和详细信息
        print(str(e))


if __name__ == '__main__':

    # 打开网络摄像头播放
    url1 = 'rtmp://127.0.0.1:1935/1p/test'
    url2 = 'rtmp://127.0.0.1:1935/2p/test'

    thread1 = threading.Thread(target=OpenAndPlayVideoFile,args=(url1,1))
    thread1.start()

    thread2 = threading.Thread(target=OpenAndPlayVideoFile, args=(url2,2))
    thread2.start()

    thread3 = threading.Thread(target=rec)
    thread3.start()

    thread4 = threading.Thread(target=refresh)
    thread4.start()

    while(True):
        bias = int(input("请输入偏置:"))
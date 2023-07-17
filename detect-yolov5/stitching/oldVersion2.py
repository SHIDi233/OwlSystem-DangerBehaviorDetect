import cv2
import imutils
import numpy as np
from PIL import ImageChops

import SIFT.Sift2 as ss

from stitching.SIFT.Panorama import Stitcher


# 打开本地视频文件或网络摄像头，并播放视频
def OpenAndPlayVideoFile(filename1,filename2):
    total=0
    vw = None

    i=0

    try:

        print("======to start play video file...")
        # 使用OpenCV自带的VideoCapture()函数定义视频文件对象，网络摄像机本质上可以看作远程网络视频文件
        cap = cv2.VideoCapture(filename1)
        cap_2 = cv2.VideoCapture(filename2)

        stitcher = Stitcher()
        # stitcher = cv2.Stitcher_create()

        # 循环读取每一帧
        while (cap.isOpened() and cap_2.isOpened()):
            total = total + 1
            # 第一个返回值result是一个布尔值，表示当前这一帧是否获取正确
            (result, result_2), (frame, frame_2) = zip(cap.read(), cap_2.read())
            # ImageChops.screen(frame, frame_2)
            # print(type(result_2))
            # print(type(frame_2))
            # result_2, frame_2 = cap_2.read()
            # 读取视频文件结束时，退出播放
            if not result:
                print('play end...')
                break


            # 在名称为“video file”的窗口中，显示视频
            # cv2.imshow("video file", frame)
            #cv2.imshow("aa", True)
            # cv2.imshow("video file2", frame_2)
            # 当按下"q"或“Q”键时，退出播放
            # cv2.waitKey(1)
            # frame.show()
            # imageA = cv2.imread(frame)
            # imageB = cv2.imread(frame_2)
            frame = imutils.resize(frame, width=850)
            frame_2 = imutils.resize(frame_2, width=850)

            f = np.hstack((frame, frame_2))
            # print(type(imageA))
            # stitch the images together to create a panorama

            (result, vis) = stitcher.stitch([frame, frame_2],total, showMatches=True)
            # (result1, result) = ss.sift(frame, frame_2)
            # show the images
            # cv2.imshow("Image A", imageA)
            # cv2.imshow("Image B", imageB)
            cv2.imshow("Keypoint Matches", result)
            # cv2.imshow("Result", result)
            cv2.waitKey(1)

            if total==1 :
                fps, w, h = 30, result.shape[1], result.shape[0]
                vw = cv2.VideoWriter("testVid.mp4", cv2.VideoWriter_fourcc(*'mp4v'), fps, (w, h))
                vw.write(result)
            else:
                vw.write(result)


        # 释放视频文件或摄像头资源
        vw.release()
        cap.release()
        cap_2.release()
        # 销毁所有窗口，释放资源
        cv2.destroyAllWindows()
        print("end...")
    except Exception as e:
        # 访问异常的错误编号和详细信息
        print(str(e))


if __name__ == '__main__':
    # # 打开本地文件播放
    # video_file = 'opencv.mp4'
    # OpenAndPlayVideoFile(video_file)

    # 打开本地0号摄像头播放
    # OpenAndPlayLocalCamera(0)

    # 打开网络摄像头播放
    url1 = '11.mp4'
    url2 = '12.mp4'

    OpenAndPlayVideoFile(url1, url2)
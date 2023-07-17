import os
import cv2
DATA_DIR = '../data'
OUTPUT_DIR = '../output'
SCALE_FACTOR = 1/2

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

        # 循环读取每一帧
        while (cap.isOpened() and cap_2.isOpened()):
            total = total + 1
            # 第一个返回值result是一个布尔值，表示当前这一帧是否获取正确
            (result, result_2), (frame, frame_2) = zip(cap.read(), cap_2.read())

            frame = cv2.resize(frame, dsize=(0, 0),
                               fx=SCALE_FACTOR, fy=SCALE_FACTOR)
            frame_2 = cv2.resize(frame_2, dsize=(0, 0),
                               fx=SCALE_FACTOR, fy=SCALE_FACTOR)

            images = []
            images.append(frame)
            images.append(frame_2)


            stitcher = cv2.Stitcher_create(cv2.STITCHER_SCANS)
            if len(images) <=1:
                continue
            status, stitched = stitcher.stitch(images)
            if status == 0:
                # out_path = os.path.join(OUTPUT_DIR, 'stitched.png')
                # cv2.imwrite(out_path, stitched)
                stitched = cv2.resize(stitched, dsize=(0, 0),
                                   fx=10*SCALE_FACTOR, fy=10*SCALE_FACTOR)
                cv2.imshow("Stitched", stitched)
                cv2.waitKey(1)
            else:
                print("Stitching failed, retake images")

            # 读取视频文件结束时，退出播放
            if not result:
                print('play end...')
                break

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
    # 打开网络摄像头播放
    url1 = '11.mp4'
    url2 = '12.mp4'

    OpenAndPlayVideoFile(url1, url2)
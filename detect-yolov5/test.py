import cv2
import subprocess
# 打开摄像头
cap = cv2.VideoCapture(0)

# 设置摄像头分辨率
cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
cap.set(cv2.CAP_PROP_BUFFERSIZE, 2)

fps = cap.get(cv2.CAP_PROP_FPS)
print("fps:", fps)
# 设置缓冲区大小为2

# 定义视频编码器
fourcc = cv2.VideoWriter_fourcc(*'X264')

# 创建FFmpeg命令行参数
ffmpeg_cmd = ['ffmpeg',
              '-y',  # 覆盖已存在的文件
              '-f', 'rawvideo',
              '-pixel_format', 'bgr24',
              '-video_size', '640x480',
              '-i', '-',  # 从标准输入读取数据
              '-vcodec', 'libx264', #使用x264编码器
              '-tune','zerolatency',
              '-pix_fmt', 'yuv420p',
              '-f', 'flv',
              'rtmp://127.0.0.1:1935/live/test']

# ffmpeg_cmd = ['ffmpeg',
#               '-re',  # 覆盖已存在的文件
#               '-i', 'E:\ST\视频\灵魂潮汐.mp4',  # 从标准输入读取数据
#               '-vcodec', 'libx264', #使用x264编码器
#               '-acodec','aac',
#               '-f', 'flv',
#               'rtmp://127.0.0.1:1935/live/test']

# 启动FFmpeg进程
ffmepg_process = subprocess.Popen(ffmpeg_cmd, stdin=subprocess.PIPE)
# 开始采集和推流
while True:
    # 采集一帧图像
    ret, frame = cap.read()
    if ret:
        ffmepg_process.stdin.write(frame)
        # 通过FFmpeg编码和推流


# 停止FFmpeg进程并释放资源
ffmepg_process.stdin.close()
ffmepg_process.wait()
cap.release()

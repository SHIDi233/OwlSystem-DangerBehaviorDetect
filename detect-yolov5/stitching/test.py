import cv2
import numpy as np
from PIL import Image


def stitch(image):
    # 图像拼接
    # stitcher = cv2.createStitcher(False)  # OpenCV 3.X.X.X使用该方法
    stitcher = cv2.Stitcher_create(cv2.Stitcher_PANORAMA)  # OpenCV 4.X.X.X使用该方法，cv2.Stitcher_create()也可以
    status, pano = stitcher.stitch(image)

    # 黑边处理
    if status == cv2.Stitcher_OK:
        # 全景图轮廓提取
        stitched = cv2.copyMakeBorder(pano, 10, 10, 10, 10, cv2.BORDER_CONSTANT, (0, 0, 0))
        gray = cv2.cvtColor(stitched, cv2.COLOR_BGR2GRAY)
        thresh = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY)[1]
        cnts = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[0]

        # 轮廓最小正矩形
        mask = np.zeros(thresh.shape, dtype="uint8")
        (x, y, w, h) = cv2.boundingRect(cnts[0])  # 取出list中的轮廓二值图，类型为numpy.ndarray
        cv2.rectangle(mask, (x, y), (x + w, y + h), 255, -1)

        # 腐蚀处理，直到minRect的像素值都为0
        minRect = mask.copy()
        sub = mask.copy()
        while cv2.countNonZero(sub) > 0:
            minRect = cv2.erode(minRect, None)
            sub = cv2.subtract(minRect, thresh)

        # 提取minRect轮廓并裁剪
        cnts = cv2.findContours(minRect, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[0]
        (x, y, w, h) = cv2.boundingRect(cnts[0])
        stitched = stitched[y:y + h, x:x + w]

        cv2.imshow('stitched', stitched)
        cv2.imwrite('stitched.jpg', stitched)
        cv2.waitKey(0)
        cv2.destroyAllWindows()
    else:
        print('图像匹配的特征点不足')


image1 = cv2.imread('1.jpg')
image2 = cv2.imread('2.jpg')
image3 = cv2.imread('3.jpg')
# image4 = cv2.imread('D:\pythonProject1\danger-behivior-detect-ai\yolov5-master\img\img4.jpg')
image = image1, image2, image3 # image4
stitch(image)

stitcher = cv2.Stitcher_create(cv2.Stitcher_PANORAMA)  # OpenCV 4.X.X.X使用该方法，cv2.Stitcher_create()也可以
status, pano = stitcher.stitch(image)

im = Image.fromarray(pano)
im.save('newfile4.png')

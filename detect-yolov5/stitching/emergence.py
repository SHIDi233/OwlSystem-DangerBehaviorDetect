import cv2

# 读取待拼接图像
img1 = cv2.imread('1.jpg')
img2 = cv2.imread('2.jpg')

# 创建stitcher对象并将两张图像拼接在一起
stitcher = cv2.Stitcher_create(cv2.STITCHER_SCANS)
(status, stitched) = stitcher.stitch((img1, img2))

# 显示拼接后的图像
cv2.namedWindow("stitched image", cv2.WINDOW_NORMAL)
cv2.imshow("stitched image", stitched)
cv2.waitKey()
cv2.destroyAllWindows()

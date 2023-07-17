# 导入所需的库
import cv2
import numpy as np

# 读取两幅图像
img1 = cv2.imread('img1.jpg')
img2 = cv2.imread('img2.jpg')

# 创建LoFTR对象
loftr = cv2.SIFT_create()

# 提取两幅图像的特征匹配
matches = loftr.match(img1, img2)

# 筛选出高置信度的匹配
good_matches = [m for m in matches if m.confidence > 0.8]

# 获取匹配点的坐标
src_pts = np.float32([img1[m.queryIdx].pt for m in good_matches]).reshape(-1, 1, 2)
dst_pts = np.float32([img2[m.trainIdx].pt for m in good_matches]).reshape(-1, 1, 2)

# 计算单应矩阵
H, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC)

# 获取图像的宽度和高度
h1, w1 = img1.shape[:2]
h2, w2 = img2.shape[:2]

# 计算图像的四个角在变换后的坐标
corners1 = np.float32([[0, 0], [0, h1], [w1, h1], [w1, 0]]).reshape(-1, 1, 2)
corners2 = np.float32([[0, 0], [0, h2], [w2, h2], [w2, 0]]).reshape(-1, 1, 2)
corners2_ = cv2.perspectiveTransform(corners2, H)

# 计算拼接后的图像的大小和偏移量
min_x = min(corners1[:,0,0].min(), corners2_[:,0,0].min())
max_x = max(corners1[:,0,0].max(), corners2_[:,0,0].max())
min_y = min(corners1[:,0,1].min(), corners2_[:,0,1].min())
max_y = max(corners1[:,0,1].max(), corners2_[:,0,1].max())
width = int(round(max_x - min_x))
height = int(round(max_y - min_y))
offset_x = int(round(min_x))
offset_y = int(round(min_y))

# 构造平移矩阵
T = np.array([[1, 0, -offset_x], [0, 1, -offset_y], [0, 0, 1]])

# 对图像进行变换和拼接
img1_ = cv2.warpPerspective(img1, T, (width, height))
img2_ = cv2.warpPerspective(img2, T.dot(H), (width, height))
pano = img1_.copy()
mask = img2_ > 0
pano[mask] = img2_[mask]

# 使用小波变换进行图像融合
pano_gray = cv2.cvtColor(pano,cv2.COLOR_BGR2GRAY)
mask_gray = mask.astype(np.uint8) * 255
pano_gray[mask_gray == 255] = 255

_, thresh = cv2.threshold(pano_gray ,254 ,255 ,cv2.THRESH_BINARY)
contours,_=cv2.findContours(thresh,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_NONE)

max_area=0
for i in range(len(contours)):
    cnt=contours[i]
    area=cv2.contourArea(cnt)
    if(area>max_area):
        max_area=area
        max_cnt=cnt

x,y,w,h=cv2.boundingRect(max_cnt)
center=(x+w//4,y+h//4)

mask_c=cv2.getStructuringElement(cv2.MORPH_ELLIPSE,(w//4,h//4))
mask_l=np.zeros((h//4,w//4),np.uint8)
mask_r=np.zeros((h//4,w//4),np.uint8)

mask_l[:,:w//8]=255
mask_r[:,w//8:]=255

mask_l=cv2.bitwise_and(mask_l ,mask_c)
mask_r=cv2.bitwise_and(mask_r ,mask_c)

mask_l=cv2.copyMakeBorder(mask_l ,0 ,0 ,w//8 ,w//8 ,cv2.BORDER_CONSTANT,value=0)
mask_r=cv2.copyMakeBorder(mask_r ,0 ,0 ,w//8 ,w//8 ,cv2.BORDER_CONSTANT,value=0)

mask_l=cv2.getRotationMatrix2D((w//4,w//4),90,1)
mask_r=cv2.getRotationMatrix2D((w//4,w//4),-90,1)

mask_l=cv2.warpAffine(mask_c ,mask_l ,(w//2,h//2))
mask_r=cv2.warpAffine(mask_c ,mask_r ,(w//2,h//2))

mask_l=mask_l[y:y+h,x:x+w]
mask_r=mask_r[y:y+h,x:x+w]

pano_left=pano[y:y+h,x:x+w]
pano_right=img1_[y:y+h,x:x+w]

pano_left=cv2.cvtColor(pano_left,cv2.COLOR_BGR2GRAY)
pano_right=cv2.cvtColor(pano_right,cv2.COLOR_BGR2GRAY)

pano_left=np.float32(pano_left)
pano_right=np.float32(pano_right)

coeff_l=cv2.dct(pano_left)
coeff_r=cv2.dct(pano_right)

coeff=np.zeros_like(coeff_l)
coeff[:,:w//4]=coeff_l[:,:w//4]
coeff[:,w//4:]=coeff_r[:,w//4:]

coeff=cv2.idct(coeff)
coeff=cv2.normalize(coeff,None,0,255,cv2.NORM_MINMAX)
coeff=np.uint8(coeff)

result=pano.copy()
result[y:y+h,x:x+w]=cv2.cvtColor(coeff,cv2.COLOR_GRAY2BGR)

# 显示和保存结果
cv2.imshow('img1', img1)
cv2.imshow('img2', img2)
cv2.imshow('result', result)
cv2.imwrite('result.jpg', result)
cv2.waitKey()
cv2.destroyAllWindows()
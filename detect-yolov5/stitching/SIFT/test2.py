# 全景拼接
import cv2
import os


# 导入文件
def Import_Files():
    # 1、主文件夹
    mainFolder = 'Scenes'
    # 2、子文件夹
    folders = os.listdir(mainFolder)

    # 3、得到子文件（图片）
    for folder in folders:
        ImageList = []
        path = mainFolder + '/' + folder  # 子文件夹路径
        Images = os.listdir(path)  # 获取文件夹下的图片名称列表（List）

        # 得到图片
        for Img in Images:
            Image = cv2.imread(f'{path}/{Img}')  # 图片     f:格式化字符串
            # 把图片放入图片列表
            ImageList.append(Image)

        # 4、缝合图片
        stitcher = cv2.Stitcher.create()  # 创建缝合器
        status, result = stitcher.stitch(ImageList)  # 对图片列表进行缝合
        # 判断是否成功
        if status == cv2.STITCHER_OK:
            print('Success!')
            cv2.imshow('result', result)
            cv2.waitKey(5000)
        else:
            print('Error!')


if __name__ == '__main__':
    Import_Files()  # 导入文件

    cv2.waitKey(0)  # 无限延时
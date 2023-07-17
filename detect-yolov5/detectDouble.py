import datetime
from timeit import default_timer as timer
import cv2
import subprocess

import numpy as np
# import numpy as np
import requests
import json

# from watchFile import watching
import time
import threading
import argparse
import os
import platform
import sys
from pathlib import Path
import torch
from oss.oss import oss_upload_file
import pandas as pd

FILE = Path(__file__).resolve()
ROOT = FILE.parents[0]  # YOLOv5 root directory
if str(ROOT) not in sys.path:
    sys.path.append(str(ROOT))  # add ROOT to PATH
ROOT = Path(os.path.relpath(ROOT, Path.cwd()))  # relative

cid=11
afps = 24

fourcc = cv2.VideoWriter_fourcc(*'X264')

# 创建FFmpeg命令行参数

hl1 = 0  # 监测区域高度距离图片顶部比例
wl1 = 0  # 监测区域高度距离图片左部比例
hl2 = 0  # 监测区域高度距离图片顶部比例
wl2 = 10 / 10  # 监测区域高度距离图片左部比例
hl3 = 10 / 10  # 监测区域高度距离图片顶部比例
wl3 = 10 / 10  # 监测区域高度距离图片左部比例
hl4 = 10 / 10  # 监测区域高度距离图片顶部比例
wl4 = 0  # 监测区域高度距离图片左部比例


ffmpeg_cmd = ['ffmpeg',
                  '-re',
                  '-r', '26',
                  '-y',  # 覆盖已存在的文件
                  '-f', 'rawvideo',
                  '-pixel_format', 'bgr24',
                  '-video_size', '1700x478',
                  '-i', '-',  # 从标准输入读取数据
                  '-vcodec', 'libx264',  # 使用x264编码器
                  '-tune', 'zerolatency',
                  '-pix_fmt', 'yuv420p',
                  '-f', 'flv',
                  'rtmp://127.0.0.1:1935/{}/test'.format(cid)]

pushImg=[]

def push():
    # global pushImg
    #
    #
    # while(True):
    #     time.sleep(0.02)
    #     if len(pushImg)==0:
    #         continue
    #
    #     pushImg.pop(0)
    pass


def changeMask():
    global hl1
    global wl1
    global hl2
    global wl2
    global hl3
    global wl3
    global hl4
    global wl4
    while(True):
        params = {'cID':cid}
        response = requests.get('http://116.204.11.171:8080/getZone', params=params)
        print(response.json())
        # loads = json.loads(response.content.decode('utf-8'))
        # data = json.loads(response.json())
        str = response.json()['data']
        if(str=='null'):
            time.sleep(1)
            continue
        strList = str.split(',')
        hl1 = float(strList[1]) # 监测区域高度距离图片顶部比例
        wl1 = float(strList[0])  # 监测区域高度距离图片左部比例
        hl2 = float(strList[3] ) # 监测区域高度距离图片顶部比例
        wl2 = float(strList[2]  )# 监测区域高度距离图片左部比例
        hl3 = float(strList[5])  # 监测区域高度距离图片顶部比例
        wl3 =float(strList[4])  # 监测区域高度距离图片左部比例
        hl4 = float(strList[7])  # 监测区域高度距离图片顶部比例
        wl4 = float(strList[6])  # 监测区域高度距离图片左部比例
        time.sleep(5)
    pass

def function1(df, csvPath, start, fps, vidName, csvName):
    df.to_csv(csvPath)
    params = {'startTime': start, 'fps': fps, 'cID': cid, 'videoUrl': oss_upload_file(Path(str("runs/detect/test/")+vidName)),
              'xlsUrl': oss_upload_file(Path(str("runs/detect/test/") + csvName))}
    requests.get('http://116.204.11.171:8080/upgrade', params=params)
    pass

def send(sTime, cTime, cID, Type, cnt):
    params = {'sTime': sTime, 'cTime': cTime, 'cID': cID,'type': Type,'cnt': cnt}
    requests.get('http://116.204.11.171:8080/sus3', params=params)
    pass

def save(df, fileName):
    df.to_csv(fileName)
    pass



from models.common import DetectMultiBackend
from utils.dataloaders import IMG_FORMATS, VID_FORMATS, LoadImages, LoadScreenshots, LoadStreams
from utils.general import (LOGGER, Profile, check_file, check_img_size, check_imshow, check_requirements, colorstr, cv2,
                           increment_path, non_max_suppression, print_args, scale_boxes, strip_optimizer, xyxy2xywh)
from utils.plots import Annotator, colors, save_one_box
from utils.torch_utils import select_device, smart_inference_mode


def isCalling(smoke, person):
    x0 = (int(smoke[0].item()) + int(smoke[2].item())) / 2
    y0 = (int(smoke[1].item()) + int(smoke[3].item())) / 2
    x1 = (int(person[0].item()) + int(person[2].item())) / 2
    y1 = (int(person[1].item()) + int(person[3].item())) / 2
    if abs(x1 - x0) < abs(int(person[0].item()) - int(person[1].item())) / 2:
        return True
    return False

def isSmoking(smoke, person):
    x0 = (int(smoke[0].item()) + int(smoke[2].item())) / 2
    y0 = (int(smoke[1].item()) + int(smoke[3].item())) / 2
    x1 = (int(person[0].item()) + int(person[2].item())) / 2
    y1 = (int(person[1].item()) + int(person[3].item())) / 2
    if abs(x1 - x0) < abs(int(person[0].item()) - int(person[1].item())) / 2:
        return True
    return False

@smart_inference_mode()
def run(
        weights=ROOT / 'yolov5s.pt',  # model path or triton URL
        source='{}p'.format(str(cid)),  # file/dir/URL/glob/screen/0(webcam)
        data=ROOT / 'data/coco128.yaml',  # dataset.yaml path
        imgsz=(640, 640),  # inference size (height, width)
        conf_thres=0.25,  # confidence threshold
        iou_thres=0.45,  # NMS IOU threshold
        max_det=1000,  # maximum detections per image
        device='',  # cuda device, i.e. 0 or 0,1,2,3 or cpu
        view_img=False,  # show results
        save_txt=False,  # save results to *.txt
        save_conf=False,  # save confidences in --save-txt labels
        save_crop=False,  # save cropped prediction boxes
        nosave=False,  # do not save images/videos
        classes=None,  # filter by class: --class 0, or --class 0 2 3
        agnostic_nms=False,  # class-agnostic NMS
        augment=False,  # augmented inference
        visualize=False,  # visualize features
        update=False,  # update all models
        project=ROOT / 'runs/detect',  # save results to project/name
        name='exp',  # save results to project/name
        exist_ok=False,  # existing project/name ok, do not increment
        line_thickness=3,  # bounding box thickness (pixels)
        hide_labels=False,  # hide labels
        hide_conf=False,  # hide confidences
        half=False,  # use FP16 half-precision inference
        dnn=False,  # use OpenCV DNN for ONNX inference
        vid_stride=1,  # video frame-rate stride
        port=12000,
):
    thread1 = threading.Thread(target=push)
    thread1.start()
    thread2 = threading.Thread(target=changeMask)
    thread2.start()

    ffmepg_process = subprocess.Popen(ffmpeg_cmd, stdin=subprocess.PIPE)

    while True:
        lastseen = 1

        source = str(source)
        save_img = not nosave and not source.endswith('.txt')  # save inference images
        is_file = Path(source).suffix[1:] in (IMG_FORMATS + VID_FORMATS)
        is_url = source.lower().startswith(('rtsp://', 'rtmp://', 'http://', 'https://'))
        webcam = source.isnumeric() or source.endswith('.streams') or (is_url and not is_file)
        screenshot = source.lower().startswith('screen')
        if is_url and is_file:
            source = check_file(source)  # download

        # Directories
        save_dir = Path('runs\\detect\\test')
        (save_dir / 'labels' if save_txt else save_dir).mkdir(parents=True, exist_ok=True)  # make dir


        # Load model
        device = select_device(device)
        model = DetectMultiBackend(weights, device=device, dnn=dnn, data=data, fp16=half)
        model_2 = DetectMultiBackend(ROOT / "models/smoke.engine", device=device, dnn=dnn, data=ROOT / "data/smoke.yaml", fp16=half)
        model_3 = DetectMultiBackend(ROOT / "models/best.engine", device=device, dnn=dnn, data=ROOT / "data/myvoc.yaml", fp16=half)
        model_4 = DetectMultiBackend(ROOT / "models/phoneCall.engine", device=device, dnn=dnn, data=ROOT / "data/phoneCall.yaml",fp16=half)

        stride, names, names_2, names_3, names_4, pt = model.stride, model.names, model_2.names, model_3.names, model_4.names, model.pt
        imgsz = check_img_size(imgsz, s=stride)  # check image size

        # Dataloader
        bs = 1  # batch_size
        if webcam:
            view_img = check_imshow(warn=True)
            dataset = LoadStreams(source, img_size=imgsz, stride=stride, auto=pt, vid_stride=vid_stride)
            bs = len(dataset)
        elif screenshot:
            dataset = LoadScreenshots(source, img_size=imgsz, stride=stride, auto=pt)
        else:
            dataset = LoadImages(source, img_size=imgsz, stride=stride, auto=pt, vid_stride=vid_stride)
        vid_path, vid_writer = [None] * bs, [None] * bs

        # Run inference
        model.warmup(imgsz=(1 if pt or model.triton else bs, 3, *imgsz))  # warmup
        model_2.warmup(imgsz=(1 if pt or model_2.triton else bs, 3, *imgsz))  # warmup
        model_3.warmup(imgsz=(1 if pt or model_3.triton else bs, 3, *imgsz))  # warmup
        model_4.warmup(imgsz=(1 if pt or model_4.triton else bs, 3, *imgsz))  # warmup
        seen, windows, dt = 0, [], (Profile(), Profile(), Profile())

        df = []

        call_weigh = 0.0
        call_add = 1.0
        call_sub = 0.5
        call_num = 0
        call_min = 30
        call_max = 400
        call_cnt = 0
        call_last = False
        call_date = datetime.datetime.now()

        smoke_weigh = 0.0
        smoke_add = 1.0
        smoke_sub = 0.5
        smoke_num = 0
        smoke_min = 10
        smoke_max = 400
        smoke_cnt = 0
        smoke_last = False
        smoke_date = datetime.datetime.now()

        down_weigh = 0.0
        down_add = 1.0
        down_sub = 0.5
        down_num = 0
        down_min = 200
        down_max = 400
        down_cnt = 0
        down_last = False
        down_date = datetime.datetime.now()

        for path, im, im0s, vid_cap, s in dataset:

            # mask for certain region
            # 1,2,3,4 分别对应左上，右上，右下，左下四个点


            for b in range(0, im.shape[0]):
                mask = np.zeros([im[b].shape[1], im[b].shape[2]], dtype=np.uint8)
                # mask[round(img[b].shape[1] * hl1):img[b].shape[1], round(img[b].shape[2] * wl1):img[b].shape[2]] = 255
                pts = np.array([[int(im[b].shape[2] * wl1), int(im[b].shape[1] * hl1)],  # pts1
                                [int(im[b].shape[2] * wl2), int(im[b].shape[1] * hl2)],  # pts2
                                [int(im[b].shape[2] * wl3), int(im[b].shape[1] * hl3)],  # pts3
                                [int(im[b].shape[2] * wl4), int(im[b].shape[1] * hl4)]], np.int32)
                mask = cv2.fillPoly(mask, [pts], (255, 255, 255))
                imgc = im[b].transpose((1, 2, 0))
                imgc = cv2.add(imgc, np.zeros(np.shape(imgc), dtype=np.uint8), mask=mask)
                # cv2.imshow('1',imgc)
                im[b] = imgc.transpose((2, 0, 1))

            start = timer()
            fullstart = start

            with dt[0]:
                im = torch.from_numpy(im).to(model.device)
                im = im.half() if model.fp16 else im.float()  # uint8 to fp16/32
                im /= 255  # 0 - 255 to 0.0 - 1.0
                if len(im.shape) == 3:
                    im = im[None]  # expand for batch dim

            # Inference
            with dt[1]:
                visualize = increment_path(save_dir / Path(path).stem, mkdir=True) if visualize else False
                pred = model(im, augment=augment, visualize=visualize)
                pred_2 = model_2(im, augment=augment, visualize=visualize)
                pred_3 = model_3(im, augment=augment, visualize=visualize)
                pred_4 = model_4(im, augment=augment, visualize=visualize)


            # NMS
            with dt[2]:
                pred = non_max_suppression(pred, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)
                pred_2 = non_max_suppression(pred_2, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)
                pred_3 = non_max_suppression(pred_3, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)
                pred_4 = non_max_suppression(pred_4, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)


            rq = False
            sd = False
            cy = False

            # Process predictions
            for (i, det), (i_2, det_2), (i_3, det_3), (i_4, det_4) in zip(enumerate(pred), enumerate(pred_2), enumerate(pred_3), enumerate(pred_4)):  # per image

                seen += 1
                pk = str(seen)
                if webcam:  # batch_size >= 1
                    p, im0, frame = path[i], im0s[i].copy(), dataset.count
                    s += f'{i}: '
                else:
                    p, im0, frame = path, im0s.copy(), getattr(dataset, 'frame', 0)

                cv2.putText(im0, "Detection_Region", (int(im0.shape[1] * wl1 - 5), int(im0.shape[0] * hl1 - 5)),
                            cv2.FONT_HERSHEY_SIMPLEX,
                            1.0, (255, 255, 0), 2, cv2.LINE_AA)
                pts = np.array([[int(im0.shape[1] * wl1), int(im0.shape[0] * hl1)],  # pts1
                                [int(im0.shape[1] * wl2), int(im0.shape[0] * hl2)],  # pts2
                                [int(im0.shape[1] * wl3), int(im0.shape[0] * hl3)],  # pts3
                                [int(im0.shape[1] * wl4), int(im0.shape[0] * hl4)]], np.int32)  # pts4

                zeros = np.zeros((im0.shape), dtype=np.uint8)
                mask = cv2.fillPoly(zeros, [pts], color=(0, 165, 255))

                # 信息
                p = Path(p)  # to Path
                save_path = str(save_dir / pk)  # im.jpg
                txt_path = str(save_dir / 'labels' / p.stem) + (
                    '' if dataset.mode == 'image' else f'_{frame}')  # im.txt
                s += '%gx%g ' % im.shape[2:]  # print string
                im0 = cv2.addWeighted(im0, 1, mask, 0.2, 0)
                cv2.polylines(im0, [pts], True, (255, 255, 0), 3)

                gn = torch.tensor(im0.shape)[[1, 0, 1, 0]]  # normalization gain whwh
                imc = im0.copy() if save_crop else im0  # for save_crop
                annotator = Annotator(im0, line_width=line_thickness, example=str(names))
                if len(det):
                    # Rescale boxes from img_size to im0 size
                    det[:, :4] = scale_boxes(im.shape[2:], det[:, :4], im0.shape).round()
                    det_2[:, :4] = scale_boxes(im.shape[2:], det_2[:, :4], im0.shape).round()
                    det_3[:, :4] = scale_boxes(im.shape[2:], det_3[:, :4], im0.shape).round()
                    det_4[:, :4] = scale_boxes(im.shape[2:], det_4[:, :4], im0.shape).round()


                    # Print results
                    # for c in det[:, 5].unique():
                    #     n = (det[:, 5] == c).sum()  # detections per class
                    #     s += f"{n} {names[int(c)]}{'s' * (n > 1)}, "  # add to string
                    #
                    # for c in det_2[:, 5].unique():
                    #     n = (det_2[:, 5] == c).sum()  # detections per class
                    #     s += f"{n} {names_2[int(c)]}{'s' * (n > 1)}, "  # add to string
                    #
                    # for c in det_3[:, 5].unique():
                    #     n = (det_3[:, 5] == c).sum()  # detections per class
                    #     s += f"{n} {names_3[int(c)]}{'s' * (n > 1)}, "  # add to string
                    #
                    # for c in det_4[:, 5].unique():
                    #     n = (det_4[:, 5] == c).sum()  # detections per class
                    #     s += f"{n} {names_4[int(c)]}{'s' * (n > 1)}, "  # add to string

                    isPeo = False

                    det_list = list()

                    count = 0

                    # Write results
                    for *xyxy, conf, cls in reversed(det):

                        isPeo = True

                        # print(names[int(cls)])
                        if names[int(cls)]!= 'person':
                            continue
                        # 信息
                        x0 = (int(xyxy[0].item()) + int(xyxy[2].item())) / 2
                        y0 = (int(xyxy[1].item()) + int(xyxy[3].item())) / 2  # 中心点坐标(x0, y0)
                        object_name = names[int(cls)]  # 获取标签名
                        info = str(x0) + ',' + str(y0)
                        det_list.append([object_name, xyxy, conf])  # 将标签位置存入数组
                        # print(info)
                        rq = True
                        c = int(cls)
                        label = None if hide_labels else (names[c] if hide_conf else f'{names[c]} {conf:.2f}')

                        if save_img or save_crop or view_img:  # Add bbox to image
                            x = 0
                            y = 0
                            distance = 99999
                            object_name3 = ''
                            conf3 = 0
                            flag = 0
                            for *xyxy2, conf2, cls2 in reversed(det_3):
                                # 信息
                                x02 = (int(xyxy2[0].item()) + int(xyxy2[2].item())) / 2
                                y02 = (int(xyxy2[1].item()) + int(xyxy2[3].item())) / 2  # 中心点坐标(x0, y0)
                                if(abs(x-x02)+abs(y-y02)<distance and (names_3[int(cls2)]=="down" or names_3[int(cls2)]=='person')):
                                    flag = 1
                                    x=x02
                                    y=y02
                                    distance=abs(x-x02)+abs(y-y02)
                                    object_name3 = names_3[int(cls2)]  # 获取标签名
                                    conf3=conf2
                                    det_list.append([object_name3, xyxy2, conf2])  # 将标签位置存入数组

                            flag=0
                            if object_name3=='person':
                                object_name3='stand'
                            if flag == 0 and conf3>0.4:
                                label = label+','+object_name3+':'+f'{conf3:.2f}'

                        annotator.box_label(xyxy, label, color=colors(c, True))
                        count += 1
                        # if save_crop:
                        #     save_one_box(xyxy, imc, file=save_dir / 'crops' / names[c] / f'{p.stem}.jpg', BGR=True)
                    if isPeo:# 多模型检测
                        for *xyxy, conf, cls in reversed(det_2):
                            # 信息
                            x0 = (int(xyxy[0].item()) + int(xyxy[2].item())) / 2
                            y0 = (int(xyxy[1].item()) + int(xyxy[3].item())) / 2  # 中心点坐标(x0, y0)
                            object_name = names_2[int(cls)]  # 获取标签名
                            info = str(x0) + ',' + str(y0)
                            det_list.append([object_name, xyxy, conf])  # 将标签位置存入数组
                            # print(info)
                            sd = True

                            if save_img or save_crop or view_img:  # Add bbox to image
                                c = int(cls)  # integer class
                                label = None if hide_labels else (names_2[c] if hide_conf else f'{names_2[c]} {conf:.2f}')
                                annotator.box_label(xyxy, label, color=colors(c+1, True))

                        for *xyxy, conf, cls in reversed(det_4):
                            # 信息
                            x0 = (int(xyxy[0].item()) + int(xyxy[2].item())) / 2
                            y0 = (int(xyxy[1].item()) + int(xyxy[3].item())) / 2  # 中心点坐标(x0, y0)
                            object_name = names_4[int(cls)]  # 获取标签名
                            info = str(x0) + ',' + str(y0)
                            det_list.append([object_name, xyxy, conf])  # 将标签位置存入数组
                            # print(info)
                            # sd = True

                            if save_img or save_crop or view_img:  # Add bbox to image
                                c = int(cls)  # integer class
                                label = None if hide_labels else (names_4[c] if hide_conf else f'{names_4[c]} {conf:.2f}')
                                annotator.box_label(xyxy, label, color=colors(c+2, True))


                    # 行为检测
                    # 判断是否有人打电话
                    isCall = False
                    # 每帧递减
                    if call_weigh >= call_sub and call_cnt <= call_max:
                        call_weigh = call_weigh - call_sub
                    elif call_num > 0:  # 记录
                        f = open('record.txt', 'a')
                        f.write(
                            datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S') + '在' + call_date.strftime(
                                '%Y-%m-%d,%H:%M:%S') + '检测到' + str(call_num) + '次打电话行为')
                        # print(datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S') + '在' + call_date.strftime(
                        #         '%Y-%m-%d,%H:%M:%S') + '检测到' + str(call_num) + '次打电话行为')
                        sTime=call_date.strftime('%Y-%m-%d,%H:%M:%S')
                        cTime = datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S')
                        cID=1
                        Type = 'calling'
                        cnt = str(call_num)
                        thread3 = threading.Thread(target=send, args=(sTime, cTime, cID, Type, cnt))
                        thread3.start()
                        f.close()
                        call_weigh = 0.0
                        call_num = 0

                    for l in det_list:
                        if l[0] == 'calling':
                            for p in det_list:
                                if p[0] == 'person':
                                    if isCalling(l[1], p[1]):
                                        isCall = True
                                        # print('detected')
                                        if call_weigh < call_sub:
                                            call_date = datetime.datetime.now()
                                        call_weigh = call_weigh + call_add * l[2] + call_sub
                                        break
                        if isCall:
                            break
                    if call_weigh < call_min:
                        call_cnt += 1
                    else:
                        call_cnt = 0

                    if not call_last and call_weigh > call_min:
                        # print('add')
                        call_num = call_num + 1
                        call_last = True
                    elif call_last and call_weigh < call_min:
                        # print('reset')
                        call_last = False
                    # print(call_weigh)

                    # 判断是否有人抽烟
                    isSmoke = False
                    # 每帧递减
                    if smoke_weigh >= smoke_sub and smoke_cnt <= smoke_max:
                        smoke_weigh = smoke_weigh - smoke_sub
                    elif smoke_num > 0:  # 记录
                        f = open('record.txt', 'a')
                        f.write(
                            datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S') + '在' + smoke_date.strftime(
                                '%Y-%m-%d,%H:%M:%S') + '检测到' + str(smoke_num) + '次打电话行为')
                        # print(datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S') + '在' + call_date.strftime(
                        #         '%Y-%m-%d,%H:%M:%S') + '检测到' + str(call_num) + '次打电话行为')
                        sTime = smoke_date.strftime('%Y-%m-%d,%H:%M:%S')
                        cTime = datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S')
                        cID = 1
                        Type = 'smoking'
                        cnt = str(smoke_num)
                        thread3 = threading.Thread(target=send, args=(sTime, cTime, cID, Type, cnt))
                        thread3.start()
                        f.close()
                        smoke_weigh = 0.0
                        smoke_num = 0

                    for l in det_list:
                        if l[0] == 'smoking':
                            for p in det_list:
                                if p[0] == 'person':
                                    if isSmoking(l[1], p[1]):
                                        isSmoke = True
                                        # print('detected')
                                        if smoke_weigh < smoke_sub:
                                            smoke_date = datetime.datetime.now()
                                        smoke_weigh = smoke_weigh + smoke_add * l[2] + smoke_sub
                                        break
                        if isSmoke:
                            break
                    if smoke_weigh < smoke_min:
                        smoke_cnt += 1
                    else:
                        smoke_cnt = 0

                    if not smoke_last and smoke_weigh > smoke_min:
                        # print('add')
                        smoke_num = smoke_num + 1
                        smoke_last = True
                    elif smoke_last and smoke_weigh < smoke_min:
                        # print('reset')
                        smoke_last = False
                    # print(call_weigh)

                    # 判断是否有人跌倒
                    isDown = False
                    # 每帧递减
                    if down_weigh >= down_sub and down_cnt <= down_max:
                        down_weigh = down_weigh - down_sub
                    elif down_num > 0:  # 记录
                        f = open('record.txt', 'a')
                        f.write(
                            datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S') + '在' + down_date.strftime(
                                '%Y-%m-%d,%H:%M:%S') + '检测到' + str(down_num) + '次打电话行为')
                        # print(datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S') + '在' + call_date.strftime(
                        #         '%Y-%m-%d,%H:%M:%S') + '检测到' + str(call_num) + '次打电话行为')
                        sTime = down_date.strftime('%Y-%m-%d,%H:%M:%S')
                        cTime = datetime.datetime.now().strftime('%Y-%m-%d,%H:%M:%S')
                        cID = 1
                        Type = 'down'
                        cnt = str(down_num)
                        thread3 = threading.Thread(target=send, args=(sTime, cTime, cID, Type, cnt))
                        thread3.start()
                        f.close()
                        down_weigh = 0.0
                        down_num = 0

                    for l in det_list:
                        if l[0] == 'down':
                            isDown = True
                            # print('detected')
                            if down_weigh < down_sub:
                                down_date = datetime.datetime.now()
                            down_weigh = down_weigh + down_add * l[2] + down_sub
                            break
                    if down_weigh < down_min:
                        down_cnt += 1
                    else:
                        down_cnt = 0

                    if not down_last and down_weigh > down_min:
                        # print('add')
                        down_num = down_num + 1
                        down_last = True
                    elif down_last and down_weigh < down_min:
                        # print('reset')
                        down_last = False
                    # print(call_weigh)


                # Stream results
                im0 = annotator.result()
                ffmepg_process.stdin.write(im0)
                if view_img:
                    if platform.system() == 'Linux' and p not in windows:
                        windows.append(p)
                        cv2.namedWindow(str(p), cv2.WINDOW_NORMAL | cv2.WINDOW_KEEPRATIO)  # allow window resize (Linux)
                        #                    cv2.resizeWindow(str(p), im0.shape[1], im0.shape[0])
                        cv2.resizeWindow(str(p), 1280, 720)
                    # cv2.imshow("1", im0)
                    # cv2.waitKey(1)
                # end = timer()
                # text = 'count = %d, FPS=%d' % (count, 1000 * (end - fullstart))
                # cv2.putText(im0, text, (40, 100), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 4)



                # Save results (image with detections)
                if save_img:
                    if dataset.mode == 'image':
                        cv2.imwrite(save_path, im0)
                    else:  # 'video' or 'stream'
                        if seen==1:

                            tm = datetime.datetime.now().strftime('%Y-%m-%d-%H-%M-%S')

                            vid_path[i] = save_path
                            if vid_cap:  # video
                                fps = vid_cap.get(cv2.CAP_PROP_FPS)
                                w = int(vid_cap.get(cv2.CAP_PROP_FRAME_WIDTH))
                                h = int(vid_cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
                            else:  # stream
                                fps, w, h = 30, im0.shape[1], im0.shape[0]

                            # print(seen)
                            save_path = str(save_dir / (tm+'-'+pk))
                            # print(save_path)
                            save_path = str(Path(save_path).with_suffix('.mp4'))  # force *.mp4 suffix on results videos
                            vid_writer[i] = cv2.VideoWriter(save_path, cv2.VideoWriter_fourcc('H', '2', '6', '4'), afps, (w, h))
                            lastseen = seen
                            df = pd.DataFrame({'fps':[], 'into':[], 'fall':[]})
                            df.loc[len(df)]=[seen, rq, sd]

                        if seen%1800==0:
                            # print(seen)
                            df.loc[len(df)] = [seen, rq, sd]

                            vid_writer[i].release()

                            thread1 = threading.Thread(target=function1, args=((df), ('runs/detect/test/' +tm+'-'+ str(lastseen) + '.csv'),
                                                                               (tm), (seen), (tm+'-'+str(lastseen)+'.mp4'),
                                                                               (tm+'-'+str(lastseen) + '.csv'),))
                            thread1.start()

                            tm = datetime.datetime.now().strftime('%Y-%m-%d-%H-%M-%S')
                            save_path = str(save_dir /Path(tm+'-'+str(seen)))
                            save_path = str(Path(save_path).with_suffix('.mp4'))  # force *.mp4 suffix on results videos
                            vid_writer[i] = cv2.VideoWriter(save_path, cv2.VideoWriter_fourcc('H', '2', '6', '4'), afps, (w, h))
                            df = pd.DataFrame(columns=['fps', 'into', 'fall'])
                            lastseen = seen

                        else:
                            vid_writer[i].write(im0)
                            df.loc[len(df)] = [seen, rq, sd]

            # LOGGER.info(f"{s}{'' if len(det) else '(no detections), '}{dt[1].dt * 1E3:.1f}ms")

        # Print results
        # t = tuple(x.t / seen * 1E3 for x in dt)  # speeds per image
        # LOGGER.info(f'Speed: %.1fms pre-process, %.1fms inference, %.1fms NMS per image at shape {(1, 3, *imgsz)}' % t)
        # if save_txt or save_img:
        #     s = f"\n{len(list(save_dir.glob('labels/*.txt')))} labels saved to {save_dir / 'labels'}" if save_txt else ''
        #     LOGGER.info(f"Results saved to {colorstr('bold', save_dir)}{s}")
        # if update:
        #     strip_optimizer(weights[0])  # update model (to fix SourceChangeWarning)

    vid_writer[i].release()
    thread1 = threading.Thread(target=function1,
                               args=((df), ('runs/detect/test/' + tm + '-' + str(lastseen) + '.csv'),
                                     (tm), (seen), (tm + '-' + str(lastseen) + '.mp4'),
                                     (tm + '-' + str(lastseen) + '.csv'),))
    thread1.start()


def parse_opt():
    parser = argparse.ArgumentParser()
    parser.add_argument('--weights', nargs='+', type=str, default= ROOT / 'models/yolov5s.engine', help='model path or triton URL')
    parser.add_argument('--source', type=str, default='rtmp://127.0.0.1/11p/test', help='file/dir/URL/glob/screen/0(webcam)')
    parser.add_argument('--data', type=str, default=ROOT / 'data/coco128.yaml', help='(optional) dataset.yaml path')
    parser.add_argument('--imgsz', '--img', '--img-size', nargs='+', type=int, default=[640], help='inference size h,w')
    parser.add_argument('--conf-thres', type=float, default=0.25, help='confidence threshold')
    parser.add_argument('--iou-thres', type=float, default=0.45, help='NMS IoU threshowld')
    parser.add_argument('--max-det', type=int, default=1000, help='maximum detections per image')
    parser.add_argument('--device', default='0', help='cuda device, i.e. 0 or 0,1,2,3 or cpu')
    parser.add_argument('--view-img', action='store_true', help='show results')
    parser.add_argument('--save-txt', action='store_true', help='save results to *.txt')
    parser.add_argument('--save-conf', action='store_true', help='save confidences in --save-txt labels')
    parser.add_argument('--save-crop', action='store_true', help='save cropped prediction boxes')
    parser.add_argument('--nosave', action='store_true', help='do not save images/videos')
    parser.add_argument('--classes', nargs='+', type=int, help='filter by class: --classes 0, or --classes 0 2 3')
    parser.add_argument('--agnostic-nms', action='store_true', help='class-agnostic NMS')
    parser.add_argument('--augment', action='store_true', help='augmented inference')
    parser.add_argument('--visualize', action='store_true', help='visualize features')
    parser.add_argument('--update', action='store_true', help='update all models')
    parser.add_argument('--project', default=ROOT / 'runs/detect/', help='save results to project/name')
    parser.add_argument('--name', default='test', help='save results to project/name')
    parser.add_argument('--exist-ok', action='store_true', help='existing project/name ok, do not increment')
    parser.add_argument('--line-thickness', default=3, type=int, help='bounding box thickness (pixels)')
    parser.add_argument('--hide-labels', default=False, action='store_true', help='hide labels')
    parser.add_argument('--hide-conf', default=False, action='store_true', help='hide confidences')
    parser.add_argument('--half', action='store_true', help='use FP16 half-precision inference')
    parser.add_argument('--dnn', action='store_true', help='use OpenCV DNN for ONNX inference')
    parser.add_argument('--vid-stride', type=int, default=1, help='video frame-rate stride')

    parser.add_argument('--port', type=int, default=12000, help='the port of the connection to C++')

    opt = parser.parse_args()
    opt.imgsz *= 2 if len(opt.imgsz) == 1 else 1  # expand
    # print_args(vars(opt))
    return opt


def main(opt):
    check_requirements(exclude=('tensorboard', 'thop'))
    run(**vars(opt))


if __name__ == '__main__':
    opt = parse_opt()
    main(opt)
    while True:
         time.sleep(1)

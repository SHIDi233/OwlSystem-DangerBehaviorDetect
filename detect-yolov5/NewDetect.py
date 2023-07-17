import torch
import argparse
import os
import platform
import sys
from pathlib import Path
from utils.torch_utils import select_device, smart_inference_mode
from models.common import DetectMultiBackend


FILE = Path(__file__).resolve()
ROOT = FILE.parents[0]
weights=ROOT / 'yolov5s.pt'
device=''
data=ROOT / 'data/coco128.yaml',  # dataset.yaml path
half = False
dnn = False

# Model
device = select_device(device)
# model = DetectMultiBackend(weights, device=device, dnn=dnn, data=data, fp16=half)
model = torch.hub.load("ultralytics/yolov5", "yolov5s")

# Images
img = "C:\\Users\\SHY\\Desktop\\Genshin-img\\1.png"  # or file, Path, PIL, OpenCV, numpy, list

# Inference
results = model(img)

# Results
results.show()  # or .show(), .save(), .crop(), .pandas(), etc.
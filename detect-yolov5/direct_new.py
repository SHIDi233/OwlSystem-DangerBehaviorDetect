import datetime
from timeit import default_timer as timer
import cv2
import subprocess
import requests

from watchFile import watching
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

cap = cv2.VideoCapture("rtmp://127.0.0.1/1p/test")
while (cap.isOpened()):

    result, frame = cap.read()

    if not result:
        print('play end...')
        break



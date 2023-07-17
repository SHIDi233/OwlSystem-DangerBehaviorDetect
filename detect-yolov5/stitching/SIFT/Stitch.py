# import the necessary packages
from Panorama import Stitcher
import argparse
import imutils
import cv2

# construct the argument parse and parse the arguments
# ap = argparse.ArgumentParser()
# ap.add_argument("-f", "--first", required=True,
#                 help="path to the first image")
# ap.add_argument("-s", "--second", required=True,
#                 help="path to the second image")
# args = vars(ap.parse_args())

# load the two images and resize them to have a width of 400 pixels
# (for faster processing)

imageA = cv2.imread("1.jpg")
imageB = cv2.imread("2.jpg")
imageA = imutils.resize(imageA, width=720)
imageB = imutils.resize(imageB, width=720)
# stitch the images together to create a panorama
stitcher = cv2.Stitcher()
(result, vis) = stitcher.stitch([imageA, imageB])
# show the images
# cv2.imshow("Image A", imageA)
# cv2.imshow("Image B", imageB)
cv2.imshow("Keypoint Matches", vis)
cv2.imshow("Result", result)
cv2.waitKey(0)
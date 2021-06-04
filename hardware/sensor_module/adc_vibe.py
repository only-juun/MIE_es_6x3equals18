import time
import spidev
import firebase_admin
import datetime
import sys, os
import requests
from firebase_admin import credentials
from firebase_admin import storage
from uuid import uuid4
from picamera import PiCamera

COUNT_REFRESH_INTERVAL = 30
THRESHOLD_VALUE = 200
NORMAL_CONTEXT_COUNT = 30

vibeDetectionCount = 0
reference_time = None
current_time = None

spi=spidev.SpiDev()
spi.open(0, 0)
spi.max_speed_hz=1000000

projectID = "big-box-2e5bb"
PrivateKeyPath = "./privateServiceKey.json"

cred = credentials.Certificate(PrivateKeyPath)
firebase_admin.initialize_app(cred, {'storageBucket':f"{projectID}.appspot.com"})
bucket = storage.bucket()


def fileUpload(file):
    blob = bucket.blob('image_store/'+file)
    new_token = uuid4()
    metadata = {"firebaseStorageDownloadTokens":new_token}
    blob.metadata = metadata

    blob.upload_from_filename(filename='./'+file, content_type='image/png')


def capture_image():
    basename = "test"
    suffix = datetime.datetime.now().strftime("%Y%m%d_%H%M%S") + '.png'
    filename = "_".join([basename, suffix])

    camera = PiCamera()
    camera.resolution = (640, 480)
    camera.start_preview()

    #camera.annotate_text = "Test"
    #camera.annotate_text_size = 20
    time.sleep(5)

    camera.capture('./'+filename)
    fileUpload(filename)
    camera.stop_preview()
    camera.close()


def clearAll():
    path = '.'
    os.system('rm -rf %s/*.png' % path)


def readValue():
	adc=spi.xfer2([1,8<<4,0])
	data=((adc[1]&3)<<8)+adc[2]
	return data


def detectStrangeVibe():
	global vibeDetectionCount
	
	if readValue()>THRESHOLD_VALUE:
		vibeDetectionCount += 1

	if vibeDetectionCount>=NORMAL_CONTEXT_COUNT:
		vibeDetectionCount = 0
		return True
	else:
		return False


def initCount():
	global reference_time, current_time
	global vibeDetectionCount

	if reference_time==None:
		reference_time = time.time()
		current_time = time.time()
	
	else:
		current_time = time.time()

	if (current_time-reference_time) >= COUNT_REFRESH_INTERVAL:
		reference_time = current_time
		vibeDetectionCount = 0
		#print(current_time)


while True :

	initCount()

	if detectStrangeVibe():

		vibeDetectionCount = 0
		reference_time = None

		print("Strange Vibration is Detected.")
		
		for i in range(4):
			capture_image()

		clearAll()
		continue

	time.sleep(0.05)
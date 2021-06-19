import time
import spidev
import firebase_admin
import datetime
import sys, os
import requests
import neopixel
import board
from firebase_admin import credentials
from firebase_admin import storage
from firebase_admin import firestore
from firebase_admin import messaging
from uuid import uuid4
from picamera import PiCamera

COUNT_REFRESH_INTERVAL = 20
THRESHOLD_VALUE = 300
NORMAL_CONTEXT_COUNT = 6000

vibeDetectionCount = 0
reference_time = None
current_time = None
timeStampString = ""

spi=spidev.SpiDev()
spi.open(0, 0)
spi.max_speed_hz=1000000

pixels = neopixel.NeoPixel(board.D18, 8)
pixels.fill((0, 0, 0))

projectID = "big-box-2e5bb"
currentPath = "/home/pi/6x3equals18/hardware/sensor_module/"
PrivateKeyPath = currentPath + "privateServiceKey.json"

cred = credentials.Certificate(PrivateKeyPath)
firebase_admin.initialize_app(cred, {'storageBucket':f"{projectID}.appspot.com"})
db = firestore.client()
barcode_ref = db.collection(u'box001')
bucket = storage.bucket()


def fileUpload(file):
    global currentPath
    blob = bucket.blob('image_store/'+file)
    new_token = uuid4()
    metadata = {"firebaseStorageDownloadTokens":new_token}
    blob.metadata = metadata

    blob.upload_from_filename(filename=currentPath+file, content_type='image/png')


def capture_image(imageSequence):
    global timeStampString, currentPath
    basename = timeStampString
    suffix = str(imageSequence)+".png"
    filename = "_".join([basename, suffix])

    camera = PiCamera()
    camera.resolution = (640, 480)
    camera.start_preview()

    #camera.annotate_text = "Test"
    #camera.annotate_text_size = 20
    time.sleep(5)

    camera.capture(currentPath+filename)
    fileUpload(filename)
    camera.stop_preview()
    camera.close()


def clearAll():
    global currentPath
    os.system('rm -rf %s*.png' % currentPath)


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

	if vibeDetectionCount == 0:
		reference_time = time.time()
	else:
		current_time = time.time()
		if (current_time-reference_time) >= COUNT_REFRESH_INTERVAL:
			vibeDetectionCount = 0


def uploadLog(msg, info):
	global barcode_ref, timeStampString
	currentTime = time.localtime()
	timeStampString = '%04d%02d%02d%02d%02d%02d' % (currentTime.tm_year, currentTime.tm_mon, currentTime.tm_mday, currentTime.tm_hour, currentTime.tm_min, currentTime.tm_sec)
	barcode_ref.document(u'Log').update( {f'{timeStampString}': {
	u'Code': 'None',
	u'Date': f'{timeStampString}',
	u'Event': f'{msg}',
	u'Info': f'{info}'}})


def sendCloudMessage(title, msg):
	global barcode_ref
	registration_token = barcode_ref.document("UserAccount").get({u'Token'}).to_dict()['Token']
	message = messaging.Message(
		data={"title": f'{title}', "message":f'{msg}'},
		#notification=messaging.Notification(
		#	title=f'{title}',
    		#body=f'{msg}',
		#click_action='.MainActivity'
			#),
  		token=registration_token)

	response = messaging.send(message)

def turnOnLight():
	global pixels
	pixels.fill((255, 255, 255))

def turnOffLight():
	global pixels
	pixels.fill((0, 0, 0))

while True :

	initCount()
	if detectStrangeVibe():
		vibeDetectionCount = 0
		reference_time = 0
		uploadLog("도난 시도 감지", "택배함의 이상 진동이 감지되었습니다.")
		sendCloudMessage("도난 시도 감지", "택배함의 이상 진동이 감지되었습니다.")

		turnOnLight()
		for i in range(5):
			capture_image(i)
		turnOffLight()
		clearAll()
		continue

	#time.sleep(0.05)

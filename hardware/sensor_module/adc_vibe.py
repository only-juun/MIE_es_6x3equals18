import time
import spidev
import firebase_admin
import datetime
import sys, os
import requests
from firebase_admin import credentials
from firebase_admin import storage
from firebase_admin import firestore
from firebase_admin import messaging
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
db = firestore.client()
barcode_ref = db.collection(u'box')
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


def uploadLog(msg, info):
	global barcode_ref
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
		notification=messaging.Notification(
			title=f'{title}',
    		body=f'{barcode_ref}, {msg}'
			),
  		token=registration_token)

	response = messaging.send(message)


while True :

	initCount()
	if detectStrangeVibe():
		vibeDetectionCount = 0
		reference_time = None
		uploadLog("도난 시도 감지", "택배함의 이상 진동이 감지되었습니다.")
		sendCloudMessage("도난 시도 감지", "택배함의 이상 진동이 감지되었습니다.")

		for i in range(5):
			capture_image()
		clearAll()
		continue

	time.sleep(0.05)
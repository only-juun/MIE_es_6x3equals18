#-*-coding:utf-8 -*-

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import storage
from picamera import PiCamera
from time import sleep

cred = credentials.Certificate("./big-box-2e5bb-firebase-adminsdk-opmnp-944d0558e3.json")
PROJECT_ID = "big-box-2e5bb"
firebase_admin.initialize_app(cred,{'storageBucket':f"{PROJECT_ID}.appspot.com"})
db = firestore.client()
camera = PiCamera()

def find_CodeValid(code):
    values = db.collection(u'box').where(u'code', u'==', f'{code}').stream()
    for val in values:
        return val.get(u'info')
    return 0

def cam_upload():
    bucket = storage.bucket()
    for val in range(5):
        camera.capture(f'/home/pi/Desktop/cam/capture{val}.jpg')
        blob = bucket.blob(f"test/img{val}")
        blob.upload_from_filename(f'/home/pi/Desktop/cam/capture{val}.jpg')


cam_upload()
return_val = find_CodeValid("k")
if return_val == 0:
    print("none")
else:
    print(return_val)


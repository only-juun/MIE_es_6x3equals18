#-*-coding:utf-8 -*-

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import RPi.GPIO as GPIO
import time


def lock_open(pin):
    print("open")
    GPIO.output(pin, True)

def lock_close(pin):
    GPIO.output(pin, False)
    print("close")
    
lock_pin = 16
GPIO.cleanup(lock_pin)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(lock_pin, GPIO.OUT)

cred = credentials.Certificate("./barcodedb-efafb-firebase-adminsdk-wujo4-01ed441f25.json")
firebase_admin.initialize_app(cred)
db = firestore.client()
barcode_ref = db.collection(u'bigbox')



door_open = False
invalid_access = 0

docs = barcode_ref.stream()
print(docs)
for doc in docs:
  print(u'{} => {}'.format(doc.id, doc.to_dict()))


while(1):
  scan_result = input("Barcode scaner module: ")
  # 바코드 스캔 정보가 DB에 있는지 확인해서 있는지 없는지 알게됨
  # 유효 바코드(사용자, 운송장)일 경우 door_open = True
  # 유효바코드가 아닌 경우, invalid_access++ 
  query_ref = barcode_ref.where(u'code' , u'==',scan_result).get()      # Type: List
  # print(query_ref.id) 사용 불가
  if len(query_ref)==0:        # 바코드가 저장되어있지 않은 경우
    door_open = False
    print("invalid barcode")
    invalid_access = invalid_access+1
  else:
    doc = query_ref[0]
    valid_barcode_document = doc.id
    door_open = True
    
    lock_open(lock_pin)
    time.sleep(5)
    
    # 로그(open) 추가 코드 쓰기
    
    lock_close(lock_pin)
    time.sleep(3)
    
    # 로그(close) 추가 코드 쓰기
    
    door_open = False 
    
  if invalid_access > 2:
    print("Send info to App")
    # 어플리케이션으로 알림 전송 코드 작성
    invalid_access = 0         # 초기화  
      
    
     
    
# 데이터 추가: 이용 
#doc_ref = db.collection(u'users').document(u'alovelace')
#doc_ref.set({
#    u'first': u'Ada',
#    u'last': u'Lovelace',
#    u'born': 1815
#})

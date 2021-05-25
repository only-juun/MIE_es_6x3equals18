#-*-coding:utf-8 -*-

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import RPi.GPIO as GPIO
import time

class Log_data (object):
    def __init__(self, Boxname, code, date, DocRef):
      self.Coll_ref = Boxname
      self.Doc_ref = DocRef
      self.Code = code
      self.Date = date

    def LogUpload(self, msg):
      delivery_info = self.Doc_ref.get(u'info')
      self.Coll_ref.document(u'Log').update( {f'{self.Date}': {
      u'Code': f'{self.Code}',
      u'Date': f'{self.Date}',
      u'Event': f'{msg}',
      u'Info': f'{delivery_info}' } } )

    def delete(self):
      self.Doc_ref.delete()
      

    
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

cred = credentials.Certificate("./big-box-2e5bb-firebase-adminsdk-opmnp-944d0558e3.json")
firebase_admin.initialize_app(cred)
db = firestore.client()
barcode_ref = db.collection(u'jTozyyclMEU7ja7kn5vS2O1v0xD2')



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
    time.sleep(1) # sleep(5)
    
    # 로그(open) 추가 코드 쓰기
    now = time.localtime()
    current_time = '%04d%02d%02d%02d%02d' % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min)
    
    log = Log_data(Boxname=barcode_ref, code=scan_result, date=current_time, DocRef=doc)
    log.LogUpload('택배가 도착하였으며 함이 열립니다.')  # 택배 도착에 대한 로그 추가

    lock_close(lock_pin)
    time.sleep(3)
    
    # 로그(close) 추가 코드 쓰기
    
    door_open = False
    log.LogUpload('택배 함이 닫힙니다.')  # 택배 도착에 대한 로그 추가
    log.delete()  # document 삭제
    
  if invalid_access > 2:
    print("Send info to App")
    # 어플리케이션으로 알림 전송 코드 작성
    barcode_ref.document(u'Log').update( {f'{current_time}': {
      u'Event': u'잘못된 바코드가 인식되었습니다.' } } )

    invalid_access = 0         # 초기화
    

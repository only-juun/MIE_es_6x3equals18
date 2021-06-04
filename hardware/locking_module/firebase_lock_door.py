#-*-coding:utf-8 -*-

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import RPi.GPIO as GPIO
import time

i=0
door_open = False
GPIO.setmode(GPIO.BOARD)
GPIO.cleanup()

# 로그 기록 클래스 
class Log_data (object):
    def __init__(self, Boxname, code, date, DocRef):
      self.Coll_ref = Boxname
      self.Doc_ref = DocRef
      self.Code = code
      self.Date = date

    def LogUpload(self, msg):
      delivery_info = self.Doc_ref.get(u'Info')
      self.Coll_ref.document(u'Log').update( {f'{self.Date}': {
      u'Code': f'{self.Code}',
      u'Date': f'{self.Date}',
      u'Event': f'{msg}',
      u'Info': f'{delivery_info}' } } )

    def delete(self):
      self.Doc_ref.delete()
      
# 문닫힘 감지 센서 
class mag_door():
	def __init__(self,pin):
		self.pin = pin	
		global i
		global door_open
		GPIO.setup(self.pin,GPIO.IN)
		GPIO.add_event_detect(pin,GPIO.RISING,bouncetime=100,callback=self.callback)
	def callback(self,pin):
		global door_open
		global i
		door_state =True
		i = 0
		time.sleep(0.5)

# 바코드 검색 함수
def find_CodeValid(code):
    values = db.collection(u'parkjinho').where(u'code', u'==', f'{code}').stream()
    for val in values:
        return val.get(u'Info')
    return 0
    
# 잠금장치 모듈 
class Locking_module(object):
    def __init__(self,pin):
      self.pin = pin
      GPIO.setup(self.pin,GPIO.OUT)

    def lock_open(self):
      print("open")
      GPIO.output(self.pin, True)
      #time.sleep(?)

    def lock_close(self):
      GPIO.output(self.pin, False)
      print("close")
      #time.sleep(?)


door_sensor = mag_door(37)
lock_module = Locking_module(16)

cred = credentials.Certificate("./big-box-2e5bb-firebase-adminsdk-opmnp-dfb91aa466.json")
firebase_admin.initialize_app(cred)
db = firestore.client()
barcode_ref = db.collection(u'parkjinho')      # Collection 이름 수정
time_array = [0,0,0]
docs =barcode_ref.stream() 
for doc in docs: 
	print(doc.id , doc.to_dict())

invalid_access = 0


while(1):
  scan_result = input("Barcode scaner module: ")
  # 바코드 스캔 정보가 DB에 있는지 확인해서 있는지 없는지 알게됨
  # 유효 바코드(사용자, 운송장)일 경우 door_open = True
  # 유효바코드가 아닌 경우, invalid_access++ 
  query_result = find_CodeValid(scan_result)

  if query_result==0:        # 바코드가 저장되어있지 않은 경우+ 바코드가 Invalid한 경우
      door_open = False
      print("invalid barcode")
      time_array[invalid_access] = time.time()    # 현재 시간 초단위로 저장
      invalid_access = invalid_access+1
      invalid_access_time = int(time_array[2])-int(time_array[0])
      
      if invalid_access >2:
          if invalid_access_time<10 and invalid_access_time>1:
              print("10초 이내에 유효하지않은 바코드 3회 이상 입력됨")
              #################################
              #     로그+ 알림 기능 코드 추가 #
              #################################

          # 유효하지않은 바코드가 입력 되었지만 단시간 내에 발생한 것이 아닐 때
          print("invalid_access_(time) 리셋")
          time_array = [0,0,0]
          invalid_access = 0
      
  else:
    # 인식한 바코드가 있는 경우
    door_open = True
    lock_module.lock_open()

    # 로그(open) 추가
    now = time.localtime()
    current_time = '%04d%02d%02d%02d%02d' % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min)
    
    ##################################################################
    #     로드 어디다 저장하는지 확정?, dockment 이름 알아야하는지      #
    ##################################################################

    #log = Log_data(Boxname=barcode_ref, code=scan_result, date=current_time, DocRef=doc)
    #log.LogUpload('택배가 도착하였으며 함이 열립니다.')  # 택배 도착에 대한 로그 추가

    while door_open:
      print(i)
      i=i+1
      time.sleep(1)
      if i > 5:
        lock_module.lock_close()
        door_open = False
        print("closed  ",door_open)
        break
      else:
        print("Still opened")
    
    
    
    ##################################################################################
    # 도큐먼트 삭제 or 택배 배송정보를 배송완료로 변경 코드추가(도큐먼트 id알아야함) #
    ##################################################################################

    
    # 로그(close) 추가
    #door_open = False 
    #log.LogUpload('택배 함이 닫힙니다.')  # 택배 도착에 대한 로그 추가
      
    
     
    
# 데이터 추가: 이용 
#doc_ref = db.collection(u'users').document(u'alovelace')
#doc_ref.set({
#    u'first': u'Ada',
#    u'last': u'Lovelace',
#    u'born': 1815
#})

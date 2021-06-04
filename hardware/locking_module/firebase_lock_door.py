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
    def __init__(self, Boxname, code, DocSnapshot):
      self.Coll_ref = Boxname
      self.Doc_snapshot = DocSnapshot
      self.Code = code

    def LogUpload(self, msg):
      if 'Info' in self.Doc_snapshot.to_dict():
        self.usercode = False
        delivery_info = self.Doc_snapshot.get(u'Info')
      else :
        self.usercode = True
        delivery_info = "User"
      now = time.localtime()
      current_time = '%04d%02d%02d%02d%02d%02d' % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
      self.Coll_ref.document(u'Log').update( {f'{current_time}': {
      u'Code': f'{self.Code}',
      u'Date': f'{current_time}',
      u'Event': f'{msg}',
      u'Info': f'{delivery_info}' } } )

    def change_valid(self):
      if self.usercode == False:
        self.Doc_snapshot.reference.update({u'valid': False})

    # 현재 쓰지 않는 코드로 받은 택배의 document를 삭제하는 함수
    # def delete(self):
    #   self.Doc_ref.delete()


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

    def lock_close(self):
      GPIO.output(self.pin, False)
      print("close")


door_sensor = 37                              # 문 닫힘 감지 센서 37번(Physical) 핀
GPIO.setup(door_sensor,GPIO.IN)
lock_module = Locking_module(16)              # 잠금 장치 모듈 16번 핀

cred = credentials.Certificate("./big-box-key.json")
firebase_admin.initialize_app(cred)
db = firestore.client()
barcode_ref = db.collection(u'parkjinho')      ## Collection 이름 수정 ##

time_array = [0,0,0]
docs =barcode_ref.stream() 

# 프로그램 시작전 정상동작 확인용(파이어베이스 내용 출력)
for doc in docs: 
	print(doc.id , doc.to_dict())

invalid_access = 0                              #  유효하지않은 바코드 인식 횟수 카운트


while(1):
  scan_result = input("Barcode scaner module: ")
  # 바코드 스캔 정보가 DB에 있는지 확인해서 있는지 없는지 알게됨
  # 유효 바코드(사용자, 운송장)일 경우 door_open = True
  # 유효바코드가 아닌 경우, invalid_access++ 
  query_ref = barcode_ref.where(u'code' , u'==',scan_result).get()

  if len(query_ref)==0:        # 바코드가 저장되어있지 않은 경우+ 바코드가 Invalid한 경우
      door_open = False
      print("invalid barcode")
      time_array[invalid_access] = time.time()    # 현재 시간 초단위로 저장
      invalid_access = invalid_access+1
      invalid_access_time = int(time_array[2])-int(time_array[0])
      
      if invalid_access >2:
          if invalid_access_time<10 and invalid_access_time>1:
              # 어플리케이션으로 알림 전송 코드 
              print("10초 이내에 유효하지 않은 바코드 3회 이상 입력됨")
              print("Send info to App")
              current_time = '%04d%02d%02d%02d%02d' % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min)
              barcode_ref.document(u'Log').update( {f'{current_time}': {
                u'Event': u'유효하지 않은 바코드가 3회 이상 인식되었습니다.' } } )
                #######################################################################
                #         잘못된 바코드가 인식되었다는 로그 전송은 되는것같음             #
                ######### 알림 전송하는 코드 추가 필요함    FCM                 #########
                #######################################################################

          else:
          # 유효하지않은 바코드가 입력 되었지만 단시간 내에 발생한 것이 아닐 때
            print("invalid_access_(time) 리셋")

          # 초기화
          time_array = [0,0,0]
          invalid_access = 0
      
  else:
    # 인식한 바코드가 있는 경우
    doc = query_ref[0]
    valid_barcode_document = doc.id
    if(doc.get(u'valid') == False):
      # 인식한 바코드가 있지만 Valid하지 않은 경우
      door_open = False
      print("invalid barcode")
      invalid_access = invalid_access+1
    else:
      # 로그(open) 추가
      log = Log_data(Boxname=barcode_ref, code=scan_result, DocSnapshot=doc)
      print(doc.to_dict())
      now = time.localtime()
      current_time = '%04d%02d%02d%02d%02d' % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min)
      log.LogUpload('택배가 도착하였으며 함이 열립니다.')  # 택배 도착에 대한 로그 추가
      #######################################################################
      # 위 코드에서 Current_time 로그로 저장 잘되는지 확인하고 되면 이부분 삭제  #
      # 추가로 문 열리면 알림(FCM) 전송하는지?  전송하면 알림 전송 코드도 추가해줘!
      #######################################################################

      door_open = True        # 로그 저장 후 솔레노이드 잠금 해제
      lock_module.lock_open()

    while door_open:
      #print(i)
      if GPIO.input(door_sensor)==0:  
        # 문 닫힘 감지 센서가 붙어 있는 경우(문이 닫혀있는 상태)
        i=i+1
      else:
        # 문 닫힘 감지 센서가 떨어져 있는 경우(문 열려있음)
        i=0
      time.sleep(1)

      if i > 3:
        # 택배함 문이 닫히고 3초가 지난 후에 잠금장치가 잠김
        log.LogUpload('택배 함이 닫힙니다.')  # 택배함 잠금 로그 추가
        log.change_valid()

      ###################################################################
      # 문이 닫힐때 알림(FCM) 전송하는지?  전송하면 알림 전송 코드도 추가해줘#
      ###################################################################    
       
        lock_module.lock_close()
        door_open = False
        # print("closed  ",door_open)
        i=0
        break
      # else:
      #   print("Still opened")




   
    
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

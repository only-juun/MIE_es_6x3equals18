#-*-coding:utf-8 -*-

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import messaging
import RPi.GPIO as GPIO
import time
from evdev import InputDevice, list_devices, categorize, ecodes

keys = {0:None, 1: u'ESC', 2: u'1', 3: u'2', 4: u'3', 5: u'4', 6: u'5', 7: u'6', 8: u'7', 9: u'8', 10: u'9', 11: u'0', 12: u'-', 13: u'=', 14: u'BKSP', 15: u'TAB', 16: u'Q', 17: u'W', 18: u'E', 19: u'R', 20: u'T', 21: u'Y', 22: u'U', 23: u'I', 24: u'O', 25: u'P', 26: u'[', 27: u']', 28: u'CRLF', 29: u'LCTRL', 30: u'A', 31: u'S', 32: u'D', 33: u'F', 34: u'G', 35: u'H', 36: u'J', 37: u'K', 38: u'L', 39: u':', 40: u'"', 41: u'`', 42: u'LSHFT', 43: u'\\', 44: u'Z', 45: u'X', 46: u'C', 47: u'V', 48: u'B', 49: u'N', 50: u'M', 51: u',', 52: u'.', 53: u'/', 54: u'RSHFT', 56: u'LALT', 100: u'RALT'}

i=0
door_open = False
GPIO.setmode(GPIO.BOARD)
GPIO.cleanup()

barcodeDeviceName = "SM SM-2D PRODUCT HID KBW"
devices = map(InputDevice, list_devices())
for device in devices:
  if device.name == barcodeDeviceName:
    dev = InputDevice(device.fn)

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

def scan_barcode():
  barcode = ""
  caps = False
  for event in dev.read_loop():
    if event.type == ecodes.EV_KEY:
      data = categorize(event)
      if data.scancode == 42:
        if data.keystate == 1:
          caps = True
        if data.keystate == 0:
          caps = False
      else:
        if data.keystate == 1:
          if data.scancode == 28:
            return barcode
          elif data.scancode >=2 and data.scancode <=11:
            barcode += keys[data.scancode]
          else:
            if caps:
              barcode += keys[data.scancode]
            if not caps:
              barcode += keys[data.scancode].lower()

# 바코드 검색 함수
def find_CodeValid(code):
    values = db.collection(u'box001').where(u'code', u'==', f'{code}').stream()
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
      
def uploadLog(msg, info):
	global barcode_ref
	currentTime = time.localtime()
	timeStampString = '%04d%02d%02d%02d%02d%02d' % (currentTime.tm_year, currentTime.tm_mon, currentTime.tm_mday, currentTime.tm_hour, currentTime.tm_min, currentTime.tm_sec)
	barcode_ref.document(u'Log').update( {f'{timeStampString}': {
	u'Code': 'None',
	u'Date': f'{timeStampString}',
	u'Event': f'{msg}',
	u'Info': f'{info}'}})

# FCM 전송 #
def sendCloudMessage(title, msg):
	global barcode_ref, box_name
	registration_token = barcode_ref.document("UserAccount").get({u'Token'}).to_dict()['Token']
	message = messaging.Message(
		data={"title" : f'{title}', "message" : f'{msg}', "box": f'{box_name}'},
  		token=registration_token)
	response = messaging.send(message)


door_sensor = 37                              # 문 닫힘 감지 센서 37번(Physical) 핀
GPIO.setup(door_sensor,GPIO.IN)
lock_module = Locking_module(16)              # 잠금 장치 모듈 16번 핀

cred = credentials.Certificate("/home/pi/6x3equals18/hardware/locking_module/big-box-key.json")
firebase_admin.initialize_app(cred)
db = firestore.client()
box_name = 'box001'
barcode_ref = db.collection(box_name)      ## Collection 이름 수정 ##

time_array = [0,0,0]
docs =barcode_ref.stream() 

# 프로그램 시작전 정상동작 확인용(파이어베이스 내용 출력)
for doc in docs: 
	print(doc.id , doc.to_dict())

invalid_access = 0                              #  유효하지않은 바코드 인식 횟수 카운트
time_stamp = 0

while(1):
  #scan_result = input("Barcode scaner module: ")
  prev_time = time.time() 
  scan_result = scan_barcode()
  print(scan_result)
  # 바코드 스캔 정보가 DB에 있는지 확인해서 있는지 없는지 알게됨
  # 유효 바코드(사용자, 운송장)일 경우 door_open = True
  # 유효바코드가 아닌 경우, invalid_access++ 
  query_ref = barcode_ref.where(u'code' , u'==',scan_result).get()

  if len(query_ref)==0:        # 바코드가 저장되어있지 않은 경우+ 바코드가 Invalid한 경우
      door_open = False
      print("invalid barcode")
     
      access_time = time.time() 
      if invalid_access==0:
          time_stamp = access_time
      if access_time - time_stamp <= 10:
          invalid_access = invalid_access+1
      else:
          invalid_access = 1
          time_stamp = access_time 
      if invalid_access >2:
          # 어플리케이션으로 알림 전송 코드 
          print("10초 이내에 유효하지 않은 바코드 3회 이상 입력됨")
          print("Send info to App")
          uploadLog("유효하지 않은 바코드", "유효하지 않은 바코드가 3회 이상 인식되었습니다.")
          sendCloudMessage("인증 3회 이상 실패", "유효하지 않은 바코드가 3회 이상 인식되었습니다.")
          invalid_access = 0 
  else:
    # 인식한 바코드가 있는 경우
    doc = query_ref[0]
    valid_barcode_document = doc.id
    QR = False

    if(doc.get(u'valid') == False):
      # 인식한 바코드가 있지만 Valid하지 않은 경우
      door_open = False
      access_time = time.time() 
      print("invalid barcode")
      if invalid_access==0:
         time_stamp = access_time
      if access_time - time_stamp <= 10:
         invalid_access = invalid_access +1 
      
      else:
          invalid_access = 1
          time_stamp = access_time 
      if invalid_access > 2:  
          print("10초 이내에 유효하지 않은 바코드 3회 이상 입력됨")
          print("Send info to App")
          uploadLog("유효하지 않은 바코드", "유효하지 않은 바코드가 3회 이상 인식되었습니다.")
          sendCloudMessage("인증 3회 이상 실패", "유효하지 않은 바코드가 3회 이상 인식되었습니다.")
          invalid_access = 0 
    else:
      # 로그(open) 추가
      log = Log_data(Boxname=barcode_ref, code=scan_result, DocSnapshot=doc)
      print(doc.to_dict())
      now = time.localtime()
      current_time = '%04d%02d%02d%02d%02d' % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min)
      log.LogUpload('택배함이 열립니다.')  # 택배 도착에 대한 로그 추가
      
      door_open = True        # 로그 저장 후 솔레노이드 잠금 해제
      lock_module.lock_open()
      print(time.time() - prev_time)

      if doc.id == "QRcode":
        QR = True
      if not QR:
        info = doc.get(u'Info')
        sendCloudMessage("택배 도착", "도착 택배 정보 : {}".format(info))
      invalid_access = 0

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
       
        lock_module.lock_close()
        door_open = False
        # print("closed  ",door_open)
        i=0
        break
      # else:
      #   print("Still opened")




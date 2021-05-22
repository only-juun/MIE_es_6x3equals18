import RPi.GPIO as GPIO
import time

class Sw801(object):
	def __init__(self,pin):
		GPIO.setmode(GPIO.BOARD)
		self.pin = pin	
		GPIO.setup(self.pin,GPIO.IN)
		GPIO.add_event_detect(self.pin,GPIO.BOTH,bouncetime=100,callback=self.callback)
		self.count=0
	def callback(self,pin):
		self.count+=1
	def detect(self):
		# Detect V
		print("Detected")
	def undetect(self):
		print("Not Detected")

	
# Set pin number name
module=Sw801(40)
sensitivity = 10

try:
	while True:
		time.sleep(1)
		if module.count >= sensitivity:
			module.detect()
		else:
			module.undetect()
		module.count=0
except KeyboardInterrupt:
	# Return resource, Before exit
	GPIO.cleanup()


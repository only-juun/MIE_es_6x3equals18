#-*-coding:utf-8 -*-
import RPi.GPIO as GPIO
import time


GPIO.cleanup()
i=0
door_state = False # openned = true 
GPIO.setmode(GPIO.BOARD)

class mag_door():
	def __init__(self,pin):
		self.pin = pin	
		GPIO.setup(self.pin,GPIO.IN)
		global door_state
		global i
		GPIO.add_event_detect(pin,GPIO.RISING,bouncetime=100,callback=self.callback)
	def callback(self,pin):
		global door_state
		global i
		i = 0
		door_state =True
		print("Detected")
		#time.sleep(0.5)

	
# Set pin number name

door_sensor=mag_door(37)

try:
	while True:
		print(i)
		i=i+1
		time.sleep(1)
		if i > 5:
			door_state = False
			print("closed  ",door_state)
			# break ?ë‹¨ ??
		else:
			print("Still opened")		


except KeyboardInterrupt:
	# Return resource, Before exit
	GPIO.cleanup()


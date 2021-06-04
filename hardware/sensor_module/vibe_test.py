import RPi.GPIO as GPIO
import time

# Set pin number name
GPIO.setmode(GPIO.BOARD)  # Real Pin num 
#GPIO.setmode(GPIO.BCM)     # Pin Name

# Set input/output pin 
#GPIO.setup(Pin_num, GPIO.OUT)
channel = 40 
GPIO.setup(channel,GPIO.IN)

def callback(channel):
	if GPIO.input(channel):
		print(GPIO.input(channel))	
	else:
		print("~~")

GPIO.add_event_detect(channel, GPIO.BOTH, bouncetime=300)
GPIO.add_event_callback(channel, callback)
time.sleep(2)

while True:
#	state=GPIO.input(channel)
#	print(state)
	time.sleep(1)

# Return resource, Before exit
GPIO.cleanup()

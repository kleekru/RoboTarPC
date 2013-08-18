package ioio.RoboTar.PCconsole;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOConnectionManager.Thread;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RoboTarIOIOforPCConsole extends IOIOConsoleApp {
	private boolean ledOn_ = false;

	// Boilerplate main(). Copy-paste this code into any IOIOapplication.
	public static void main(String[] args) throws Exception {
		new RoboTarIOIOforPCConsole().go(args);
	}

	@Override
	protected void run(String[] args) throws IOException {
		RoboTarStartPage window = new RoboTarStartPage();
		window.mainstart(args);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("Could not load RoboTar User Interface");
			e.printStackTrace();
		}
		System.out.println("Start of the Run method");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		boolean abort = false;
		String line;
				
		while (!abort && (line = reader.readLine()) != null) {
			if (line.equals("q")) {
				abort = true;
				System.out.println("Quitting. ");
			} else {
				System.out
						.println("Unknown input. Q=quit.");
			}
		}
		System.out.println("End of the Run method");
		}

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {
			private DigitalOutput led_;

			private final int I2C_PAIR = 0; //IOIO Pair for I2C
			private static final float FREQ = 50.0f;
			private static final int PCA_ADDRESS = 0x40;
			private static final byte PCA9685_MODE1 = 0x00;
			private static final byte PCA9685_PRESCALE = (byte) 0xFE;
			private final int BUTTON1_PIN = 34;
			private DigitalInput mButton1;
			//private DigitalInput mButton2;
			
			private TwiMaster twi_;
			
			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				System.out.println("Start of the BaseIOIOLooper.setup method");
				mButton1 = ioio_.openDigitalInput(BUTTON1_PIN, DigitalInput.Spec.Mode.PULL_UP); // Setup Input Button 1
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
				twi_ = ioio_.openTwiMaster(I2C_PAIR, TwiMaster.Rate.RATE_1MHz, false); // Setup IOIO TWI Pins	
				reset();
			}

			private void reset() throws ConnectionLostException,
			InterruptedException {
				// Set prescaler - see PCA9685 data sheet
				System.out.println("Start of the BaseIOIOLooper.reset method");
				float prescaleval = 25000000;
				prescaleval /= 4096;
				prescaleval /= FREQ;
				prescaleval -= 1;
				byte prescale = (byte) Math.floor(prescaleval + 0.5);
				
				write8(PCA9685_MODE1, (byte) 0x10); // go to sleep... prerequisite to set prescaler
				write8(PCA9685_PRESCALE, prescale); // set the prescaler
				write8(PCA9685_MODE1, (byte) 0x20); // Wake up and set Auto Increment
			}
			
			void write8(byte reg, byte val) throws ConnectionLostException,
				InterruptedException {
				System.out.println("Start of the write8 method");
				byte[] request = {reg, val};
				twi_.writeReadAsync(PCA_ADDRESS, false, request, request.length, null, 0);
			}
		
			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				System.out.println("Start of the loop method");
				led_.write(!ledOn_);
				//Thread.sleep(10);
				System.out.println("Made it to the Loop");
				/*Chords[] chordreceived = new Chords[6];
				BufferedReader reader2 = new BufferedReader(new InputStreamReader(
						System.in));
				boolean abort2 = false;
				String line;*/
				
				boolean reading1 = mButton1.read();
				//boolean reading2 = mButton1.read();
				/**
				 * Logic that determines chord note maps to servo address and direction
				 * Map of string and fret locations to channel and servo position:
				 * Low E String Frets 1 & 2 = Servo 0 Channel 0
				 * Low E String Frets 3 & 4 = Servo 1 Channel 1
				 * A String Frets 1 & 2 = Servo 2 Channel 2
				 * A STring Frets 3 & 4 = Servo 3 Channel 3
				 * D String Frets 1 & 2 = Servo 4 Channel 4
				 * D String Frets 3 & 4 = Servo 5 Channel 5
				 * G String Frets 1 & 2 = Servo 6 Channel 6
				 * G String Frets 3 & 4 = Servo 7 Channel 7
				 * B String Frets 1 & 2 = Servo 8 Channel 8
				 * B String Frets 3 & 4 = Servo 9 Channel 9
				 * High E String Frets 1 & 2 = Servo 10 Channel 10
				 * High E String Frets 3 & 4 = Servo 11 Channel 11
				 * 
				 * Position
				 * Neutral for all = 1.0
				 * Top 3 strings right = 1.5, left = 0.0
				 * Bottom 3 strings right = 0.0, left = 1.5
				 * 
				 * setServo method will take the parameters (Channel, Position)
				 * 
				 * A chord will be defined as Channel[6], position [3]
				 *  
				 */
				
				System.out.println("Value of Button Push OUT of While Loop: " + reading1);
				//Thread.sleep(10);
				while (!reading1) {
					//if (!reading1){
					//if (line.equals("n")) {
						reading1 = mButton1.read();
					//	reading2 = mButton1.read();
						System.out.println("Value of Button Push 1 IN ON While Loop: " + reading1);
						//System.out.println("Value of Button Push 2 IN ON While Loop: " + reading2);
					
						ledOn_ = true;
						System.out.println("Low E Channel Value with IOIO Class: " + Chords.getChannelLowE()+ "Low E Position value with IOIO Class: "+ Chords.getLowEstringPosition());
						System.out.println("A Channel Value with IOIO Class: " + Chords.getChannelA()+ "A Position value with IOIO Class: "+ Chords.getAStringPosition());
						System.out.println("D Channel Value with IOIO Class: " + Chords.getChannelD()+ "D Position value with IOIO Class: "+ Chords.getDStringPosition());
						System.out.println("G Channel Value with IOIO Class: " + Chords.getChannelG()+ "G Position value with IOIO Class: "+ Chords.getGStringPosition());
						System.out.println("B Channel Value with IOIO Class: " + Chords.getChannelB()+ "B Position value with IOIO Class: "+ Chords.getBStringPosition());
						System.out.println("High E Channel Value with IOIO Class: " + Chords.getChannelHighE()+ "High E Position value with IOIO Class: "+ Chords.getHighEStringPosition());
						setServo(Chords.getChannelLowE(), Chords.getLowEstringPosition());
						setServo(Chords.getChannelA(), Chords.getAStringPosition());
						setServo(Chords.getChannelD(), Chords.getDStringPosition());
						setServo(Chords.getChannelG(), Chords.getGStringPosition());
						setServo(Chords.getChannelB(), Chords.getBStringPosition());
						setServo(Chords.getChannelHighE(), Chords.getHighEStringPosition());
				} 
				
				//while (reading2) {
					System.out.println("Value of Button Push 1 IN OFF While Loop: " + reading1);
					//System.out.println("Value of Button Push 2 IN OFF While Loop: " + reading2);
					ledOn_ = false;
					setServo(0, 1.0f);
					setServo(1, 1.0f);
					setServo(2, 1.0f);
					setServo(3, 1.0f);
					setServo(4, 1.0f);
					setServo(5, 1.0f);
					setServo(6, 1.0f);
					setServo(7, 1.0f);
					setServo(8, 1.0f);
					setServo(9, 1.0f);
					setServo(10, 1.0f);
					setServo(11, 1.0f);
					setServo(12, 1.0f);
					System.out.println("Servos in neutral position default");
				//}
				/*
				for (int i=0;i<chordreceived.length;i++)
					{
					System.out.println("Value of chordreceived in RoboTarforPCIOIOSwing = "+chordreceived[i]);
					//System.out.println("Value of ChordReceived in RoboTarforPCIOIOSwing = "+chordSend[i]);
					}
				//Thread.sleep(1000);
				int lowEstringIn = Chords.getLowEstringReceive();
				System.out.println(lowEstringIn);
				int AstringIn = Chords.getAstringReceive();
				System.out.println(AstringIn);
				int DstringIn = Chords.getDstringReceive();
				System.out.println(DstringIn);
				int GstringIn = Chords.getGstringReceive();
				System.out.println(GstringIn);
				int BstringIn = Chords.getBstringReceive();
				System.out.println(BstringIn);
				int highEstringIn = Chords.getHighEstringReceive();
				System.out.println(highEstringIn);
			
				Thread.sleep(10);
				System.out.println("Made it to the loop further down");
				Chords[] chordreceived1 = new Chords[6];
											
				for (int i=0;i<chordreceived1.length;i++)
					{
					System.out.println("Value of chordreceived in RoboTarforPCIOIOSwing = "+chordreceived1[i]);
					//System.out.println("Value of ChordReceived in RoboTarforPCIOIOSwing = "+chordSend[i]);
					}
		
			//All Servos one way
			setServo(0, 0.0f);
			setServo(1, 0.0f);
			setServo(2, 0.0f);
			setServo(3, 0.0f);
			setServo(4, 0.0f);
			setServo(5, 0.0f);
			setServo(6, 0.0f);
			setServo(7, 0.0f);
			setServo(8, 0.0f);
			setServo(9, 0.0f);
			setServo(10, 0.0f);
			setServo(11, 0.0f);
			setServo(12, 0.0f);
			setServo(13, 0.0f);
			setServo(14, 0.0f);
			setServo(15, 0.0f);
			Thread.sleep(1000);
			//All Servos back the other way
			setServo(0, 1.5f);
			setServo(1, 1.5f);
			setServo(2, 1.5f);
			setServo(3, 1.5f);
			setServo(4, 1.5f);
			setServo(5, 1.5f);
			setServo(6, 1.5f);
			setServo(7, 1.5f);
			setServo(8, 1.5f);
			setServo(9, 1.5f);
			setServo(10, 1.5f);
			setServo(11, 1.5f);
			setServo(12, 1.5f);
			setServo(13, 1.5f);
			setServo(14, 1.5f);
			setServo(15, 1.5f);
			Thread.sleep(1000);
			
			//PWM Range below is 0.0. to 1.5.  Cycle through each servo channel.
			for (int c=0; c<16; c++) {
				for (float p = 1.5f; p>0.0; p-=0.5f) {
					Thread.sleep(200);
					setServo(c, p);
					led_.write(ledOn_);
				}
			
				for (float p=0.0f; p<1.5f; p+=0.5f) {
					Thread.sleep(200);
					setServo(c, p);
				}
			}*/
				
			}
				public void setServo(int servoNum, float pos) throws ConnectionLostException, InterruptedException {
					//Set Servo channel and milliseconds input to PulseWidth calculation
					setPulseWidth(servoNum, pos + 1.0f);  //
				}
				
				public void setPulseWidth(int channel, float ms) throws ConnectionLostException, InterruptedException {
					// Set pulsewidth according to PCA9685 data sheet based on milliseconds value sent from setServo method
					// 4096 steps per cycle, frequency is 50MHz (50 steps per millisecond)
					int pw = Math.round(ms / 1000 * FREQ * 4096);
					// Skip to every 4th address value to turn off the pulse (see datasheet addresses for LED#_OFF_L)
					byte[] request = { (byte) (0x08 + channel * 4), (byte) pw, (byte) (pw >> 8) };
					twi_.writeReadAsync(PCA_ADDRESS, false, request, request.length, null, 0);
				}
			
		};
	}
}

/** Kevin's Commit and Check in Test for Git**/
package ioio.robotar.pcconsole;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class.
 * Manages IOIO console loop and starts RoboTar GUI. 
 */
public class RoboTarIOIOforPCConsole extends IOIOConsoleApp {
	static final Logger LOG = LoggerFactory.getLogger(RoboTarIOIOforPCConsole.class);
	
	private boolean ledOn_ = false;

	private RoboTarStartPage robotarGUI;

	// Boilerplate main(). Copy-paste this code into any IOIOapplication.
	public static void main(String[] args) throws Exception {
		new RoboTarIOIOforPCConsole().go(args);
	}

	@Override
	protected void run(String[] args) throws IOException {
		robotarGUI = new RoboTarStartPage();
		robotarGUI.mainstart(args);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			LOG.error("Could not load RoboTar User Interface");
			e.printStackTrace();
		}
		LOG.info("Start of the Run method");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		boolean abort = false;
		String line;
				
		while (!abort && (line = reader.readLine()) != null) {
			if (line.equals("q")) {
				abort = true;
				LOG.info("Quitting. ");
			} else {
				LOG.info("Unknown input. Q=quit.");
			}
		}
		LOG.info("End of the Run method");
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
			private DigitalInput pedalButton;
			
			private TwiMaster twi_;
			
			private boolean lastKnownPedalPosition = true;
			
			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				LOG.info("IOIO is connected");
				pedalButton = ioio_.openDigitalInput(BUTTON1_PIN, DigitalInput.Spec.Mode.PULL_UP); // Setup Input Button 1
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
				twi_ = ioio_.openTwiMaster(I2C_PAIR, TwiMaster.Rate.RATE_1MHz, false); // Setup IOIO TWI Pins	
				reset();
			}

			private void reset() throws ConnectionLostException,
			InterruptedException {
				// Set prescaler - see PCA9685 data sheet
				LOG.info("Start of the BaseIOIOLooper.reset method");
				float prescaleval = 25000000;
				prescaleval /= 4096;
				prescaleval /= FREQ;
				prescaleval -= 1;
				byte prescale = (byte) Math.floor(prescaleval + 0.5);
				
				write8(PCA9685_MODE1, (byte) 0x10); // go to sleep... prerequisite to set prescaler
				write8(PCA9685_PRESCALE, prescale); // set the prescaler
				write8(PCA9685_MODE1, (byte) 0x20); // Wake up and set Auto Increment
			}
			
			private void write8(byte reg, byte val) throws ConnectionLostException,
				InterruptedException {
				LOG.info("Start of the write8 method");
				byte[] request = {reg, val};
				twi_.writeReadAsync(PCA_ADDRESS, false, request, request.length, null, 0);
			}
		
			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				LOG.info("Start of the loop method");
				led_.write(!ledOn_);

				// initial position
				// high = true, low = false
				boolean pedalInHighPosition = pedalButton.read();
				LOG.debug("current position of pedal is: {}", pedalInHighPosition);

				if (lastKnownPedalPosition == pedalInHighPosition) {
					// no change from last time
					return;
				}
				
				if (!pedalInHighPosition) {
					// PEDAL IS PRESSED
					ledOn_ = true;

					// we are checking and logging the status first
					if (robotarGUI == null) {
						LOG.error("There is no RoboTar GUI!");
					} else {
						if (robotarGUI.getChordsPage() == null) {
							LOG.debug("informative - there is no chords page");
						}
						if (robotarGUI.getSongsPage() == null) {
							LOG.debug("informative - there is no songs page");
						}
						if (robotarGUI.getServoSettings() == null) {
							// this should not happen, servo settings are initialized to neutral positions in the constructor
							LOG.warn("There is no chord chosen!");
						} else {
							// if songs page exists and we already play the song, play next chord
							if (robotarGUI.getSongsPage() != null && robotarGUI.getSongsPage().isPlaying()) {
								robotarGUI.getSongsPage().simPedalPressed();
							} else if (robotarGUI.getChordsPage() != null) {
								// if not, and chords page exists, play chord that is set in radio buttons
								robotarGUI.getChordsPage().prepareServoValues();
							}
							
							// everything is set correctly and we have servo settings available 
							// (either from songs or chords page, or default - neutral) or last one? - check
							ServoSettings chordServoValues = robotarGUI.getServoSettings();
							LOG.debug("got chord: {}", chordServoValues.debugOutput());
							long timeStart = System.currentTimeMillis();
							for (int i = 0; i < 6; i++) {
								int servoNumber = chordServoValues.getServos()[i];
								float servoValue = chordServoValues.getValues()[i];
								LOG.debug("setServo call: servo: {}, value: {}", servoNumber, servoValue);
								setServo(servoNumber, servoValue);
							}
							long timeEnd = System.currentTimeMillis();
							LOG.debug("It took {} ms to execute 6 servos", timeEnd - timeStart);
						}
					}
				} else {
					// PEDAL IS RELEASED
					// turn off led
					ledOn_ = false;
					// reset servos
					resetAllToNeutral();
					
				} 

				// save current status of the pedal
				lastKnownPedalPosition = pedalInHighPosition;
				
				/*
				 //TODO what is this?
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
			
			/**
			 * Reset all servos to neutral position.
			 * 
			 * @throws ConnectionLostException
			 * @throws InterruptedException
			 */
			public void resetAllToNeutral() throws ConnectionLostException, InterruptedException {
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
				LOG.info("Servos in neutral position default");
			}
			
			/**
			 * Set Servo channel and milliseconds input to PulseWidth calculation
			 * 
			 * @param servoNum
			 * @param pos
			 * @throws ConnectionLostException
			 * @throws InterruptedException
			 */
			public void setServo(int servoNum, float pos) throws ConnectionLostException, InterruptedException {
				setPulseWidth(servoNum, pos + 1.0f);  //
			}
			
			protected void setPulseWidth(int channel, float ms) throws ConnectionLostException, InterruptedException {
				// Set pulsewidth according to PCA9685 data sheet based on milliseconds value sent from setServo method
				// 4096 steps per cycle, frequency is 50MHz (50 steps per millisecond)
				int pw = Math.round(ms / 1000 * FREQ * 4096);
				// Skip to every 4th address value to turn off the pulse (see datasheet addresses for LED#_OFF_L)
				byte[] request = { (byte) (0x08 + channel * 4), (byte) pw, (byte) (pw >> 8) };
				twi_.writeReadAsync(PCA_ADDRESS, false, request, request.length, null, 0);
			}
			
			@Override
			public void disconnected() {
				LOG.info("IOIO disconnected");
			}

			@Override
			public void incompatible() {
				LOG.info("Incompatible firmware version of IOIO");
			}
		};
	}
}

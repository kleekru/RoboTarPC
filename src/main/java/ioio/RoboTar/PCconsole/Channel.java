package ioio.RoboTar.PCconsole;

public class Channel {

	private static int channelForServo;
	
	Channel(int channel) {
		this.channelForServo = channel;
	}
	
	public int translateChannel() {
		if (channelForServo == 61 | channelForServo == 62) {
			channelForServo = 0;
		}
		if (channelForServo == 63 | channelForServo == 64 | channelForServo == 69) {
			channelForServo = 1;
		}
		if (channelForServo == 51 | channelForServo == 52) {
			channelForServo = 2;
		}
		if (channelForServo == 53 | channelForServo == 54 | channelForServo == 59) {
			channelForServo = 3;
		}
		if (channelForServo == 41 | channelForServo == 42) {
			channelForServo = 4;
		}
		if (channelForServo == 43 | channelForServo == 44 | channelForServo == 49) {
			channelForServo = 5;
		}
		if (channelForServo == 31 | channelForServo == 32) {
			channelForServo = 6;
		}
		if (channelForServo == 33 | channelForServo == 34 | channelForServo == 39) {
			channelForServo = 7;
		}
		if (channelForServo == 21 | channelForServo == 22) {
			channelForServo = 8;
		}
		if (channelForServo == 23 | channelForServo == 24 | channelForServo == 29) {
			channelForServo = 9;
		}
		if (channelForServo == 11 | channelForServo == 12) {
			channelForServo = 10;
		}
		if (channelForServo == 13 | channelForServo == 14 | channelForServo == 19) {
			channelForServo = 11;
		}
		else {
			channelForServo = 12;  
			// TODO throw an exception if not one of the values above
		}
		
		System.out.println("This is the value of Channel from the Channel Class: " + channelForServo);
		
		return channelForServo;
		} //End channelForServo method

	public static int getChannelForServo() {
		return channelForServo;
	}

	public static void setChannelForServo(int channelForServo) {
		Channel.channelForServo = channelForServo;
	}
		
		
	}

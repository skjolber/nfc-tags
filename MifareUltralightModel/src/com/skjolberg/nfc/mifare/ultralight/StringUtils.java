package com.skjolberg.nfc.mifare.ultralight;

public class StringUtils {

    public static byte[] hexStringToByteArray (String s) {
        if ((s.length() % 2) != 0) {
            throw new IllegalArgumentException("Bad input string: " + s);
        }
        
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
	public static String bytesToHexString(byte[] bytes) {
		return bytesToHexString(bytes, true);
	}
	
	public static String bytesToHexString(byte[] bytes, boolean space) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);

		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
			if(space) {
				sb.append(' ');
			}
		}

		return sb.toString().trim().toUpperCase();
	}


}
package com.skjolberg.nfc.mifare.ultralight;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MifareUltralightFactory {

	public static MifareUltralight newInstance(byte[] bytes) throws IOException {
		
		MifareUltralight tag = new MifareUltralight();
		
		DataInputStream din = new DataInputStream(new ByteArrayInputStream(bytes));
		
		tag.read(din);
		
		return tag;
	}


}

package com.skjolberg.nfc.mifare.classic;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MifareClassicFactory {

	public static MifareClassic newInstance(byte[] bytes) throws IOException {
		
		MifareClassic tag = new MifareClassic();
		
		DataInputStream din = new DataInputStream(new ByteArrayInputStream(bytes));
		
		tag.read(din);
		
		return tag;
	}


}

package com.skjolberg.nfc.mifare.ultralight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OneTimeProgrammablePage extends DataPage {

	protected int offset;
	protected int length;

	public OneTimeProgrammablePage(int index, byte[] data) {
		this(index, data, 0, 4);
	}

	public OneTimeProgrammablePage(int index, byte[] data, int offset, int length) {
		super(data, index);
		
		this.offset = offset;
		this.length = length;
	}
	
	public OneTimeProgrammablePage() {
	}

	public boolean canWrite(byte[] destination) {
		for(int i = offset; i < offset + length; i++) {
			if(destination[i] != data[i]) {
				if((destination[i] & 0xFF) != 0) {

					// find ones in the destination which are not in the source and we cannot copy
					int diff = ((destination[i] & 0xFF) ^ (data[i] & 0xFF));
					
					if((diff & (destination[i] & 0xFF)) != 0) {
						// so some which are not the same are ones in the destination, and cannot be written
						return false;
					}
					
				}
			}
		}
		
		return true;
	}
	
	public boolean mustWrite(byte[] destination) {
		for(int i = offset; i < offset + length; i++) {
			if(destination[i] != data[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getType() {
		return MifareUltralight.PAGE_OTP;
	}

	@Override
	public void write(DataOutputStream dout) throws IOException {
		super.write(dout);
		
		dout.writeInt(offset);
		dout.writeInt(length);
	}
	
	@Override
	public void read(DataInputStream din) throws IOException {
		super.read(din);
		
		this.offset = din.readInt();
		this.length = din.readInt();
	}
	
}

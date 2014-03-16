package com.skjolberg.nfc.mifare.ultralight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Page {

	protected int index;
	
	protected byte[] data;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public abstract boolean isLocked();

	public void write(DataOutputStream dout) throws IOException {
		dout.writeInt(getType());
		dout.writeInt(index);
		dout.writeInt(data.length);
		dout.write(data);
	}
	
	public abstract int getType();

	public void read(DataInputStream din) throws IOException {
		this.index = din.readInt();
		
		this.data = new byte[din.readInt()];
		din.readFully(data);
	}

	public boolean isZero() {
		return data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 0;
	}
	

}

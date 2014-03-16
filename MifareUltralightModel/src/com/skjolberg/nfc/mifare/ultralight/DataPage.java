package com.skjolberg.nfc.mifare.ultralight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataPage extends Page {
	
	private boolean locked;

	public DataPage() {
	}
	
	public DataPage(byte[] page, int index) {
		this.data = page;
		this.index = index;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public void write(DataOutputStream dout) throws IOException {
		super.write(dout);
		
		dout.writeBoolean(locked);
	}
	
	@Override
	public void read(DataInputStream din) throws IOException {
		super.read(din);
		
		this.locked = din.readBoolean();
	}

	@Override
	public int getType() {
		return MifareUltralight.PAGE_TYPE_DATA;
	}
}

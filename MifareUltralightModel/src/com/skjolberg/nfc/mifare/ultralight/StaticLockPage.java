package com.skjolberg.nfc.mifare.ultralight;

import java.io.DataInputStream;
import java.io.IOException;

public class StaticLockPage extends OneTimeProgrammablePage {

	public static int getDataPageOffset() {
		return 3;
	}
	
	public static int getDataPageLength() {
		return 15 - 3 + 1;
	}
	
	private int lock;
	
	public StaticLockPage(byte[] source) {
		this.data = source;
		this.offset = 2;
		this.length = 2;
		
		init();
	}

	private void init() {
		lock = (data[offset] & 0xFF) | ((data[offset+1] & 0xFF) << 8);
	}
	
	public StaticLockPage() {
	}

	public boolean canWritePage(int page) {
		if(page < getDataPageOffset()) {
			if(page == 2) {
				return isLocked();
			}
			return false;
		}
		if(page > getDataPageOffset() + getDataPageLength()) {
			throw new IllegalArgumentException("Cannot write page " + page);
		}
 		int filter = 1 << page;
		
		return (lock & filter) == 0;
	}

	public boolean isLocked() {
		return (lock & 0x07) != 0;
	}
	
	public void lockPage(int page) {

 		int filter = 1 << page;
		
		lock = lock | filter;

		// write back to bytes
		data[offset] = (byte) (lock & 0xFF);
		data[offset + 1] = (byte) ((lock >> 8) & 0xFF);
	}

	@Override
	public int getType() {
		return MifareUltralight.PAGE_STATIC;
	}

	@Override
	public void read(DataInputStream din) throws IOException {
		super.read(din);
		
		init();
	}
	
	@Override
	public boolean isZero() {
		return data[offset] == 0 && data[offset + 1] == 0;
	}
}
 
package com.skjolberg.nfc.mifare.ultralight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DynamicLockPage extends OneTimeProgrammablePage {

	private int pageOffset;
	private int pageLength;
	private int pagesPerBit;
	
	private int lock;
	
	public DynamicLockPage(int index, byte[] source, int offset, int length, int pageOffset, int pageLength, int pagesPerBit) {
		super(index, source);
		this.offset = offset;
		this.length = length;
		
		this.pageOffset = pageOffset;
		this.pageLength = pageLength;
		this.pagesPerBit = pagesPerBit;
		
		lock = (source[offset] & 0xFF) | ((source[offset+1] & 0xFF) << 8);
	}
	
	public DynamicLockPage() {
	}

	public boolean canWritePage(int page) {
		if(page < pageOffset) {
			throw new IllegalArgumentException();
		}
		
		if(page > pageOffset + pageLength) {
			throw new IllegalArgumentException();
		}
		
		if(page == pageOffset + pageLength) {
			return isLocked();
		}
		
		int shift = (page - pageOffset) / pagesPerBit; // 0 for 0..3 and 1 for 4..7
		
		shift++; // first lock bit
		
		if(shift >= 4) {
			shift++; // second lock bit
		}
		
 		int filter = 1 << shift;
		
		return (lock & filter) == 0;
	}

	public boolean isLocked() {
		return (lock & 0x11) != 0 || (lock & 0xFF00) != 0;
	}
	
	@Override
	public void write(DataOutputStream dout) throws IOException {
		super.write(dout);
		
		dout.writeInt(pageOffset);
		dout.writeInt(pageLength);
		dout.writeInt(pagesPerBit);
	}
	
	@Override
	public void read(DataInputStream din) throws IOException {
		super.read(din);
		
		this.pageOffset = din.readInt();
		this.pageLength = din.readInt();
		this.pagesPerBit = din.readInt();
	}
	
	@Override
	public int getType() {
		return MifareUltralight.PAGE_DYNAMIC_LOCK;
	}

	@Override
	public boolean isZero() {
		return data[offset] == 0 && data[offset + 1] == 0;
	}
}
 
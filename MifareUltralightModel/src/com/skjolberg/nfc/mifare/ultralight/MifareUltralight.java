package com.skjolberg.nfc.mifare.ultralight;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MifareUltralight {

	public static final int PAGE_TYPE_DATA = 1;
	public static final int PAGE_DYNAMIC_LOCK = 2;
	public static final int PAGE_OTP = 3;
	public static final int PAGE_READ_ONLY= 4;
	public static final int PAGE_STATIC = 5;

	protected static final int VERSION = 0x01;
	
	protected int type;
	
	protected List<Page> pages = new ArrayList<Page>();
	
	public void add(Page page) {
		this.pages.add(page);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public byte[] getData(int index) {
		return getPage(index).getData();
	}
	
	public Page getPage(int index) {
		if(pages.size() > index) {
			Page page = pages.get(index);
			if(page.getIndex() == index) {
				return page;
			}
		}

		for(Page page : pages) {
			if(page.getIndex() == index) {
				return page;
			}
		}
		
		throw new IllegalArgumentException("No page " + index);
	}

	public int getDataCapacity() {
		int count = 0;
		
		for(Page page : pages) {
			if(page instanceof DataPage) {
				count+= 4;
			}
		}
		
		return count;
	}

	public byte[] toBytes() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		try {
			DataOutputStream dout = new DataOutputStream(bout);
			
			dout.writeInt(VERSION);
			
			dout.writeInt(type);
			dout.writeInt(pages.size());
			for(Page page : pages) {
				page.write(dout);
			}
			
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return bout.toByteArray();
	}
	
	public void read(DataInputStream din) throws IOException {
		int version = din.readInt();
		
		if(version != MifareUltralight.VERSION) {
			throw new IllegalArgumentException("Version " + version + " not supported");
		}
		
		this.type = din.readInt();
		
		int count = din.readInt();
		
		for(int i = 0; i < count; i++) {
			
			int pageType = din.readInt();

			Page page;

			switch(pageType) {
			case PAGE_TYPE_DATA : {
				page = new DataPage();
				
				break;
			}
			case PAGE_DYNAMIC_LOCK : {
				page = new DynamicLockPage();
				break;
			}
			case PAGE_OTP : {
				page = new OneTimeProgrammablePage();
				break;
			}
			case PAGE_READ_ONLY : {
				page = new ReadOnlyPage();
				break;
			}
			case PAGE_STATIC : {
				page = new StaticLockPage();
				break;
			}
			default : {
				throw new IllegalArgumentException("Unexpected page type " + pageType);
			}
			}
			
			page.read(din);
			
			this.pages.add(page);
		}
	}

	public int getDataSize() {
		int size = 0;
		
		for(Page page : pages) {
			if(page.getClass() == DataPage.class) {
				size += 4;
			}
		}
		
		return size;
	}

	public int getSize() {
		if(type == android.nfc.tech.MifareUltralight.TYPE_ULTRALIGHT) {
			return 64;
		}
		if(type == android.nfc.tech.MifareUltralight.TYPE_ULTRALIGHT_C) {
			return 168;
		}
		throw new IllegalArgumentException();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Mifare Ultralight Type " + type + ":\n");
		for(int i = 0; i < pages.size(); i++) {
			Page page = pages.get(i);

			if(page.isLocked()) {
				buffer.append("x ");
			} else {
				buffer.append(". ");
			}
			
			buffer.append(StringUtils.bytesToHexString(page.getData()));
			buffer.append(" ");
			
			buffer.append(page.getClass().getSimpleName());
			
			buffer.append("\n");
		}
		
		return buffer.toString();
	}

}

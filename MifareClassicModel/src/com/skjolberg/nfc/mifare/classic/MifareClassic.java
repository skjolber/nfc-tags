package com.skjolberg.nfc.mifare.classic;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MifareClassic {

	private static final int TYPE = 1;
	private static final int VERSION = 1;
	
	private boolean compressed = false;
	
	private List<MifareClassicSector> sectors = new ArrayList<MifareClassicSector>();

	public List<MifareClassicSector> getSectors() {
		return sectors;
	}

	public void setSectors(List<MifareClassicSector> sectors) {
		this.sectors = sectors;
	}
	
	public void read(DataInputStream din) throws IOException {
		int type = din.readInt();
		int version = din.readInt();
		if(type == TYPE && version == VERSION) {
			int count = din.readInt();
			for(int i = 0; i < count; i++) {
				MifareClassicSector sector = new MifareClassicSector();
				sector.read(din);
				sectors.add(sector);
			}
			
			compressed = din.readInt() == 1;
		} else {
			throw new IllegalArgumentException("Unexpected type " + type + " version " + version);
		}
	}
	
	public void write(DataOutputStream dout) throws IOException {
		dout.writeInt(TYPE);
		dout.writeInt(VERSION);
		
		dout.writeInt(sectors.size());
		for(MifareClassicSector sector : sectors) {
			sector.write(dout);
		}
		
		dout.writeInt(compressed ? 1 : 0);
	}
	
	public void add(MifareClassicSector mifareClassicSectorData) {
		if(mifareClassicSectorData.getIndex() == -1) throw new RuntimeException();
		
		this.sectors.add(mifareClassicSectorData);
	}
		
	public boolean validate() {
		for(MifareClassicSector sector : sectors) {
			if(!sector.validate()) {
				return false;
			}
		}
		
		return true;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Mifare Classic:\n");
		for(MifareClassicSector sector : sectors) {
			buffer.append(sector.print());
			buffer.append('\n');
		}
		if(compressed) {
			buffer.append("+ sectors with equal trailer and all zero data");
		}
		return buffer.toString();
	}
	
	public boolean isCompressed() {
		return compressed;
	}

	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	public MifareClassicSector get(int index) {
		return sectors.get(index);
	}
	
	public int getSectorCount() {
		return sectors.size();
	}

	public int getDataSize() {
		int size = 0;
		for(MifareClassicSector sector : sectors) {
			size += sector.getDataSize();
		}
		return size;
	}
	
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		DataOutputStream dout = new DataOutputStream(bout);
		try {
			write(dout);
		} finally {
			dout.close();
		}
		
		return bout.toByteArray();
	}

}


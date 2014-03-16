package com.skjolberg.nfc.mifare.classic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MifareClassicSector {
	
	private int index = -1;

	private byte[] trailerBlockKeyA;
	private byte[] trailerBlockAccessConditions;
	private byte[] trailerBlockKeyB;

	private List<byte[]> blocks = new ArrayList<byte[]>();

	public byte[] getTrailerBlockKeyA() {
		return trailerBlockKeyA;
	}

	public void setTrailerBlockKeyA(byte[] trailerBlockKeyA) {
		this.trailerBlockKeyA = trailerBlockKeyA;
	}

	public byte[] getTrailerBlockKeyB() {
		return trailerBlockKeyB;
	}

	public void setTrailerBlockKeyB(byte[] trailerBlockKeyB) {
		this.trailerBlockKeyB = trailerBlockKeyB;
	}

	public byte[] getTrailerBlockAccessConditions() {
		return trailerBlockAccessConditions;
	}

	public void setTrailerBlockAccessConditions(byte[] trailerBlockAccessConditions) {
		this.trailerBlockAccessConditions = trailerBlockAccessConditions;
	}

	public List<byte[]> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<byte[]> blocks) {
		this.blocks = blocks;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getSize() {
		int size = 4 + 4;
		
		size += 4;
		if(trailerBlockKeyA != null) {
			size += trailerBlockKeyA.length;
		}

		size += 4;
		if(trailerBlockAccessConditions != null) {
			size += trailerBlockAccessConditions.length;
		}

		size += 4;
		if(trailerBlockKeyB != null) {
			size += trailerBlockKeyB.length;
		}

		size += 4;
		for(byte[] block : blocks) {
			size += 4;
			
			size += block.length;
		}
		
		return size;
	}
	
	public void read(DataInputStream din) throws IOException {
		int size = din.readInt();
		
		din.mark(size - 4);

		index = din.readInt();
		
		int trailerBlockKeyACount = din.readInt();
		if(trailerBlockKeyACount > 0)  {
			trailerBlockKeyA = new byte[trailerBlockKeyACount];
			din.readFully(trailerBlockKeyA);
		}

		int trailerBlockAccessConditionsCount = din.readInt();
		if(trailerBlockAccessConditionsCount > 0)  {
			trailerBlockAccessConditions = new byte[trailerBlockAccessConditionsCount];
			din.readFully(trailerBlockAccessConditions);
		}

		int trailerBlockKeyBCount = din.readInt();
		if(trailerBlockKeyBCount > 0)  {
			trailerBlockKeyB = new byte[trailerBlockKeyBCount];
			din.readFully(trailerBlockKeyB);
		}

		int count = din.readInt();
		
		for(int i = 0; i < count; i++) {
			int bufferSize = din.readInt();
			byte[] buffer = new byte[bufferSize];
			din.readFully(buffer);
			
			blocks.add(buffer);
		}
		
		din.reset();
		din.skip(size - 4);
	}
	
	public void write(DataOutputStream dout) throws IOException {
		int size = getSize();
		
		dout.writeInt(size);
		dout.writeInt(index);

		if(trailerBlockKeyA != null) {
			dout.writeInt(trailerBlockKeyA.length);
			dout.write(trailerBlockKeyA);
		} else {
			dout.writeInt(0);
		}

		if(trailerBlockAccessConditions != null) {
			dout.writeInt(trailerBlockAccessConditions.length);
			dout.write(trailerBlockAccessConditions);
		} else {
			dout.writeInt(0);
		}

		if(trailerBlockKeyB != null) {
			dout.writeInt(trailerBlockKeyB.length);
			dout.write(trailerBlockKeyB);
		} else {
			dout.writeInt(0);
		}
		
		dout.writeInt(blocks.size());
		for(byte[] block : blocks) {
			dout.writeInt(block.length);
			dout.write(block);
		}

	}

	public void addBlock(byte[] block) {
		blocks.add(block);
	}

	public boolean hasTrailerBlockKeyA() {
		return trailerBlockKeyA != null;
	}

	public boolean hasTrailerBlockKeyB() {
		return trailerBlockKeyB != null;
	}

	public boolean hasTrailerBlockAccessConditions() {
		return trailerBlockAccessConditions != null;
	}

	public boolean isBlankData() {
		for(byte[] block : blocks) {
			for(int i = 0; i < block.length; i++) {
				if(block[i] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isEqualTrailer(MifareClassicSector sector) {
		if(!Arrays.equals(trailerBlockKeyA, sector.getTrailerBlockKeyA())) {
			return false;
		}
		if(!Arrays.equals(trailerBlockAccessConditions, sector.getTrailerBlockAccessConditions())) {
			return false;
		}
		if(!Arrays.equals(trailerBlockKeyB, sector.getTrailerBlockKeyB())) {
			return false;
		}
		return true;
	}
	
	public boolean isCompressable(MifareClassicSector sector) {
		return isEqualTrailer(sector) && sector.isBlankData();
	}

	public boolean validate() {
		if(!hasTrailerBlockKeyA()) {
			return false;
		}
		if(!hasTrailerBlockKeyB()) {
			return false;
		}
		
		if(!hasTrailerAccessConditions()) {
			return false;
		}
		
		return true;
	}

	private boolean hasTrailerAccessConditions() {
		return trailerBlockAccessConditions != null;
	}

	public int blockCount() {
		return blocks.size() + 1;
	}
	
	public byte[] getDataBlock(int index) {
		if(index < blocks.size()) {
			return blocks.get(index);
		}
		throw new IllegalArgumentException();
	}

	public int getDataSize() {
		int size = 16;
		for(byte[] block : blocks) {
			size += block.length;
		}
		return size;
	}
	
	public String print() {
		StringBuffer buffer = new StringBuffer();

		for(int i = 0; i < blocks.size(); i++) {
			buffer.append(printDataBlock(i));
			buffer.append('\n');
		}
		buffer.append(printTrailerBlock());
		
		return buffer.toString();
	}
	
	public String printBlock(int index) {
		if(index < blocks.size()) {
			return printDataBlock(index);
		}
		if(index == blocks.size()) {
			return printTrailerBlock();
		}
		throw new IllegalArgumentException();
	}

	private String printDataBlock(int index) {
		return StringUtils.bytesToHexString(blocks.get(index));
	}

	public String printTrailerBlock() {
		StringBuffer buffer = new StringBuffer();
		
		if(trailerBlockKeyA != null) {
			buffer.append(StringUtils.bytesToHexString(trailerBlockKeyA));
		} else {
			buffer.append("------------");
		}

		if(trailerBlockAccessConditions != null) {
			buffer.append(StringUtils.bytesToHexString(trailerBlockAccessConditions));
		} else {
			buffer.append("--------");
		}

		if(trailerBlockKeyB != null) {
			buffer.append(StringUtils.bytesToHexString(trailerBlockKeyB));
		} else {
			buffer.append("------------");
		}
		
		return buffer.toString();
	}

}


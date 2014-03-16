package com.skjolberg.nfc.mifare.ultralight;


public class ReadOnlyPage extends Page {

	public ReadOnlyPage(int index, byte[] page) {
		this.index = index;
		this.data = page;
		
	}

	public ReadOnlyPage() {
	}

	@Override
	public boolean isLocked() {
		return true;
	}

	@Override
	public int getType() {
		return MifareUltralight.PAGE_READ_ONLY;
	}

}

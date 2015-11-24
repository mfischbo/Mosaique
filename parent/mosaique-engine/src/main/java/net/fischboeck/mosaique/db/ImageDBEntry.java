package net.fischboeck.mosaique.db;

import net.fischboeck.mosaique.analyzer.Result;

class ImageDBEntry {
	
	Result _result;
	boolean _isUsed;

	public ImageDBEntry() {
	}

	ImageDBEntry(Result r) {
		this._result = r;
		this._isUsed = false;
	}
}
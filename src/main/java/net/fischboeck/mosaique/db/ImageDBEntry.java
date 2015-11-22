package net.fischboeck.mosaique.db;

import net.fischboeck.mosaique.analyzer.Result;

public class ImageDBEntry {
	
	public Result _result;
	public boolean _isUsed;

	public ImageDBEntry() {
	}

	ImageDBEntry(Result r) {
		this._result = r;
		this._isUsed = false;
	}
}

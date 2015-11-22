package net.fischboeck.mosaique;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.fischboeck.mosaique.analyzer.ImageAnalyzer;
import net.fischboeck.mosaique.analyzer.Result;
import net.fischboeck.mosaique.db.ImageDB;

public class MasterImage {

	private Result 	_result;
	private int		_tiles;
	private BufferedImage _im;

	private int		_tileWidth;
	private int		_tileHeight;
	
	public MasterImage(File f, ImageDB db, int tiles) throws Exception {
		
		ImageAnalyzer a = new ImageAnalyzer();
		this._result = a.analyze(f);

		_im = ImageIO.read(f);
		
		this._tileWidth = _result.width / tiles;
		this._tileHeight = _result.height / tiles;
		this._tiles = tiles;
	}

	public int getTileWidth() { return this._tileWidth; }
	public int getTileHeight() { return this._tileHeight; }
	public int getTileCount() { return this._tiles; }
	public Result getResult() { return this._result; }
	
	private int[] getSumAt(int tileX, int tileY) {
		
		int[] sum = new int[3];
		sum[0] = 0;
		sum[1] = 0;
		sum[2] = 0;
		
		for (int x = tileX * _tileWidth; x < (tileX * _tileWidth) + _tileWidth; ++x) {
			for (int y = tileY * _tileHeight; y < (tileY * _tileHeight) + _tileHeight; ++y) {

				int c = _im.getRGB(x, y);
				sum[0] += (c & 0x00FF0000) >> 16;
				sum[1] += (c & 0x0000FF00) >> 8;
				sum[2] += (c & 0x000000FF);
			}
		}
		return sum;
	}
	
	public int[] getAvgColor(int tileX, int tileY) {
		int q = _tileWidth * _tileHeight;
		int[] result = new int[3];
		int[] sum = getSumAt(tileX, tileY);
		result[0] = (int) sum[0] / q;
		result[1] = (int) sum[1] / q;
		result[2] = (int) sum[2] / q;
		return result;
	}
	
	public int getAvgBrightness(int tileX, int tileY) {
		int q = _tileWidth * _tileHeight;
		int[] sum = getSumAt(tileX, tileY);
		return (int) ((sum[0] + sum[1] + sum[2]) / q) / 3;
	}
}
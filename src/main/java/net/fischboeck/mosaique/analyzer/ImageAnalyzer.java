package net.fischboeck.mosaique.analyzer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.fischboeck.mosaique.analyzer.Result.Format;

public class ImageAnalyzer {


	public ImageAnalyzer () {
		
	}

	public Result analyze(File f) throws Exception {
		return analyze(new FileInputStream(f), f.getAbsolutePath());
	}
	
	public Result analyze(InputStream inStream, String path) throws Exception {
	
		Result r = new Result();
		BufferedImage img = ImageIO.read(inStream);
		r.width = img.getWidth();
		r.height = img.getHeight();
		if (r.width > r.height)
			r.format = Format.LANDSCAPE;
		else 
			r.format = Format.PORTRAIT;
	
		r.path = path;

		long rSum = 0L;
		long gSum = 0L;
		long bSum = 0L;
		
		for (int x=0; x < r.width; ++x) {
			for (int y=0; y < r.height; y++) {
				int q = img.getRGB(x, y);
			
				rSum += (q & 0x00FF0000) >> 16;
				gSum += (q & 0x0000FF00) >> 8;
				bSum += (q & 0x000000FF);
			}
		}
		
		if (rSum == gSum && gSum == bSum)
			r.isGreyscale = true;
	
		int q = r.width * r.height;
		r.avgColor = new int[3];
		r.avgColor[0] = (int) rSum / q;
		r.avgColor[1] = (int) gSum / q;
		r.avgColor[2] = (int) bSum / q;
		
		r.avgBrightness = (int) ((rSum + gSum + bSum) / q) / 3;
		return r;
	}
}

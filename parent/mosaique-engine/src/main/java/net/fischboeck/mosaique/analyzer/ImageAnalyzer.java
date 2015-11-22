package net.fischboeck.mosaique.analyzer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.fischboeck.mosaique.analyzer.Result.Format;

public class ImageAnalyzer {

	private int		sampleSteps = 4;

	public ImageAnalyzer () {
		
	}
	
	public ImageAnalyzer(int sampleSteps) {
		this.sampleSteps = sampleSteps;
	}

	public Result analyze(File f) throws Exception {
		return analyze(new FileInputStream(f), f.getAbsolutePath());
	}
	
	public static int[] toRGB(int color) {
		
		int[] r = new int[3];
		r[0] = (color & 0x00FF0000) >> 16;
		r[1] = (color & 0x0000FF00) >> 8;
		r[2] = (color & 0x000000FF);
		return r;
	}

	public static int toInt(int r, int g, int b) {
		int i = r;
		i = (i << 8) + g;
		i = (i << 8) + b;
		return i;
	}
	
	public static int toInt(int[] rgb) {
		return toInt(rgb[0], rgb[1], rgb[2]);
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

		r.color[Result.RT] = calculateAvgColor(img, r.width / 2, r.width, 0, r.height / 2);
		r.color[Result.RB] = calculateAvgColor(img, r.width / 2, r.width, r.height / 2, r.height);
		r.color[Result.LB] = calculateAvgColor(img, 0, r.width / 2, r.height / 2, r.height);
		r.color[Result.LT] = calculateAvgColor(img, 0, r.width / 2, 0, r.height / 2);
		r.color[Result.IM] = calculateAvgColor(img, 0, r.width, 0, r.height);

		r.brightness[Result.RT] = calculateBrightness(r.color[Result.RT]);
		r.brightness[Result.RB] = calculateBrightness(r.color[Result.RB]);
		r.brightness[Result.LB] = calculateBrightness(r.color[Result.LB]);
		r.brightness[Result.LT] = calculateBrightness(r.color[Result.LT]);
		r.brightness[Result.IM] = calculateBrightness(r.color[Result.IM]);
		return r;
	}
	
	
	private int calculateAvgColor(BufferedImage img, int minX, int maxX, int minY, int maxY) {

		long rSum = 0L; long gSum = 0L; long bSum = 0L;
		int smp = 0;
		
		for (int x = minX; x < maxX; x+= sampleSteps) {
			for (int y = minY; y < maxY; y+=sampleSteps) {
				
				int[] rgb = toRGB(img.getRGB(x, y));
				rSum += rgb[0];
				gSum += rgb[1];
				bSum += rgb[2];
				smp++;
			}
		}
		return toInt((int) rSum / smp, (int) gSum / smp, (int) bSum / smp);
	}

	
	private int calculateBrightness(int color) {
		int[] a = toRGB(color);
		return (a[0] + a[1] + a[2]) / 3;
	}
}
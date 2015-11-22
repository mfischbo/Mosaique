package net.fischboeck.mosaique.db;

import java.util.Comparator;

import net.fischboeck.mosaique.analyzer.ImageAnalyzer;
import net.fischboeck.mosaique.analyzer.Result;

public class ColorComparators {

	public static Comparator<ImageDBEntry> vectorBased(int color) {
		
		return new Comparator<ImageDBEntry>() {
			
			@Override
			public int compare(ImageDBEntry o1, ImageDBEntry o2) {
		
				int[] o1c = ImageAnalyzer.toRGB(o1._result.color[Result.IM]);
				int[] o2c = ImageAnalyzer.toRGB(o2._result.color[Result.IM]);
				int[] avgColor = ImageAnalyzer.toRGB(color);
				
				int r1 = avgColor[0] - o1c[0];
				int g1 = avgColor[1] - o1c[1];
				int b1 = avgColor[2] - o1c[2];
				
				double l1 = Math.sqrt(Math.pow(r1, 2) + Math.pow(g1, 2) + Math.pow(b1, 2));
				
				int r2 = avgColor[0] - o2c[0];
				int g2 = avgColor[1] - o2c[1];
				int b2 = avgColor[2] - o2c[2];
				
				double l2 = Math.sqrt(Math.pow(r2, 2) + Math.pow(g2, 2) + Math.pow(b2, 2));
				
				if (l1 > l2)
					return 1;
				if (l1 < l2)
					return -1;
				return 0;
			}
		};
	}
	
	public static Comparator<ImageDBEntry> numericBased(int color) {
		return new Comparator<ImageDBEntry>() {

			@Override
			public int compare(ImageDBEntry o1, ImageDBEntry o2) {
			
				int[] o1c = ImageAnalyzer.toRGB(o1._result.color[Result.IM]);
				int[] o2c = ImageAnalyzer.toRGB(o2._result.color[Result.IM]);
				int[] avgColor = ImageAnalyzer.toRGB(color);
				
				int xr = Math.abs(o1c[0] - avgColor[0]);
				int xg = Math.abs(o1c[1] - avgColor[1]);
				int xb = Math.abs(o1c[2] - avgColor[2]);
				
				int yr = Math.abs(o2c[0] - avgColor[0]);
				int yg = Math.abs(o2c[1] - avgColor[1]);
				int yb = Math.abs(o2c[2] - avgColor[2]);
				
				int rx = (xr + xg + xb) / 3;
				int ry = (yr + yg + yb) / 3;
				
				if (rx > ry)
					return 1;
				if (rx < ry)
					return -1;
				return 0;
			}
		};
	}
	
	
	public static Comparator<ImageDBEntry> subsampled(int[] colors) {
		
		return new Comparator<ImageDBEntry>() {

			@Override
			public int compare(ImageDBEntry o1, ImageDBEntry o2) {
		
				double[] k1 = new double[8];
				double[] k2 = new double[8];
				
				k1[0] = resultFor(colors[0], o1._result.color[Result.LT]);
				k1[1] = resultFor(colors[1], averageSum(o1._result.color[Result.LT], o1._result.color[Result.RT]));
				k1[2] = resultFor(colors[2], o1._result.color[Result.RT]);
				k1[3] = resultFor(colors[3], averageSum(o1._result.color[Result.LT], o1._result.color[Result.LB]));
				k1[4] = resultFor(colors[4], averageSum(o1._result.color[Result.RT], o1._result.color[Result.RB]));
				k1[5] = resultFor(colors[5], o1._result.color[Result.LB]);
				k1[6] = resultFor(colors[6], averageSum(o1._result.color[Result.RB], o1._result.color[Result.LB]));
				k1[7] = resultFor(colors[7], o1._result.color[Result.RB]);

				k2[0] = resultFor(colors[0], o2._result.color[Result.LT]);
				k2[1] = resultFor(colors[1], averageSum(o2._result.color[Result.LT], o2._result.color[Result.RT]));
				k2[2] = resultFor(colors[2], o2._result.color[Result.RT]);
				k2[3] = resultFor(colors[3], averageSum(o2._result.color[Result.LT], o2._result.color[Result.LB]));
				k2[4] = resultFor(colors[4], averageSum(o2._result.color[Result.RT], o2._result.color[Result.RB]));
				k2[5] = resultFor(colors[5], o2._result.color[Result.LB]);
				k2[6] = resultFor(colors[6], averageSum(o2._result.color[Result.RB], o2._result.color[Result.LB]));
				k2[7] = resultFor(colors[7], o2._result.color[Result.RB]);
				
				double r1 = reduce(k1);
				double r2 = reduce(k2);
				
				if (r1 > r2) return 1;
				if (r1 < r2) return -1;
				return 0;
			}
			
			private double reduce(double[] kernel) {
				double x = 0;
				for (double d : kernel) 
					x += d;
				return x/kernel.length;
			}
			
			private double resultFor(int a1, int a2) {
				int[] c = ImageAnalyzer.toRGB(a1);
				int[] d = ImageAnalyzer.toRGB(a2);
				
				int r = c[0] - d[0];
				int g = c[1] - d[1];
				int b = c[2] - d[2];
				return Math.sqrt(Math.pow(r, 2) + Math.pow(g, 2) + Math.pow(b, 2));
			}
			
			private int averageSum(int c1, int c2) {
				int[] q1 = ImageAnalyzer.toRGB(c1);
				int[] q2 = ImageAnalyzer.toRGB(c2);
				
				return ImageAnalyzer.toInt((int) (q1[0]+q2[0]) / 2,
						(int) (q1[1] + q2[1]) / 2,
						(int) (q1[2] + q2[2]) / 2);
			}
		};
	}
	
	
	public static Comparator<ImageDBEntry> brigthnessComparator(int brightness) {
		return new Comparator<ImageDBEntry>() {

			@Override
			public int compare(ImageDBEntry o1, ImageDBEntry o2) {
				
				int xa = Math.abs(o1._result.brightness[Result.IM] - brightness);
				int xb = Math.abs(o2._result.brightness[Result.IM] - brightness);
				
				if (xa > xb)
					return 1;
				if (xa < xb)
					return -1;
				return 0;
			}
		};
	}
}

package net.fischboeck.mosaique.db;

import java.util.Comparator;

public class ColorComparators {

	public static Comparator<ImageDBEntry> vectorBased(int[] avgColor) {
		
		return new Comparator<ImageDBEntry>() {
			
			@Override
			public int compare(ImageDBEntry o1, ImageDBEntry o2) {
			
				int r1 = avgColor[0] - o1._result.avgColor[0];
				int g1 = avgColor[1] - o1._result.avgColor[1];
				int b1 = avgColor[2] - o1._result.avgColor[2];
				
				double l1 = Math.sqrt(Math.pow(r1, 2) + Math.pow(g1, 2) + Math.pow(b1, 2));
				
				int r2 = avgColor[0] - o2._result.avgColor[0];
				int g2 = avgColor[1] - o2._result.avgColor[1];
				int b2 = avgColor[2] - o2._result.avgColor[2];
				
				double l2 = Math.sqrt(Math.pow(r2, 2) + Math.pow(g2, 2) + Math.pow(b2, 2));
				
				if (l1 > l2)
					return 1;
				if (l1 < l2)
					return -1;
				return 0;
			}
		};
	}
	
	public static Comparator<ImageDBEntry> numericBased(int[] avgColor) {
		return new Comparator<ImageDBEntry>() {

			@Override
			public int compare(ImageDBEntry o1, ImageDBEntry o2) {
				
				int xr = Math.abs(o1._result.avgColor[0] - avgColor[0]);
				int xg = Math.abs(o1._result.avgColor[1] - avgColor[1]);
				int xb = Math.abs(o1._result.avgColor[2] - avgColor[2]);
				
				int yr = Math.abs(o2._result.avgColor[0] - avgColor[0]);
				int yg = Math.abs(o2._result.avgColor[1] - avgColor[1]);
				int yb = Math.abs(o2._result.avgColor[2] - avgColor[2]);
				
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
	
	public static Comparator<ImageDBEntry> brigthnessComparator(int brightness) {
		return new Comparator<ImageDBEntry>() {

			@Override
			public int compare(ImageDBEntry o1, ImageDBEntry o2) {
				
				int xa = Math.abs(o1._result.avgBrightness - brightness);
				int xb = Math.abs(o2._result.avgBrightness - brightness);
				
				if (xa > xb)
					return 1;
				if (xa < xb)
					return -1;
				return 0;
			}
		};
	}
}

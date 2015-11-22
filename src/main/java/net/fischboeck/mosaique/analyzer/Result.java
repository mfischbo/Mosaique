package net.fischboeck.mosaique.analyzer;

public class Result {

		public final short CN	= 0;
		public final short RT	= 1;
		public final short RB	= 2;
		public final short LB	= 3;
		public final short LT	= 4;
		
		
		public enum Format { LANDSCAPE, PORTRAIT };
	
		public String   path;
		public int 		width;
		public int 		height;
	
		public Format  	format;
		
		public boolean 	isGreyscale;
		public int[]   	avgColor;
		public int		avgBrightness;
	
	
		public int[]	subAvgColor = new int[5];
		
		
		public String toString() {
			StringBuffer b = new StringBuffer("Path : ").append(path)
					.append(" [").append(width).append("x").append(height)
					.append("] ").append("\n").append(" Format: ").append(format)
					.append(" GS : " ).append(isGreyscale)
					.append(" Avg C: ").append(avgColor)
					.append(" Avg B: ").append(avgBrightness);
			return b.toString();
		}

}

package net.fischboeck.mosaique.analyzer;

public class Result {

		public static final int RT	= 0;	// avg color at right top quarter
		public static final int RB	= 1;	// avg color at right bottom quarter
		public static final int LB	= 2;	// avg color at left bottom quarter
		public static final int LT	= 3;	// avg color at left top quarter
		public static final int IM  = 4;	// overall avg color
		
		public enum Format { LANDSCAPE, PORTRAIT };
	
		public String   path;
		public int 		width;
		public int 		height;
	
		public Format  	format;
		
		public boolean 	isGreyscale;
	
		public int[]	color = new int[5];
		public int[]    brightness = new int[5];
		
		public String toString() {
			StringBuffer b = new StringBuffer("Path : ").append(path)
					.append(" [").append(width).append("x").append(height)
					.append("] ").append("\n").append(" Format: ").append(format)
					.append(" GS : " ).append(isGreyscale)
					.append(" Avg C: ").append(color);
			return b.toString();
		}
}
package net.fischboeck.mosaique;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import net.fischboeck.mosaique.analyzer.ImageAnalyzer;
import net.fischboeck.mosaique.analyzer.Result;

public class ImageAnalyzerTests {

	@Test
	public void computesAvgColorCorrectly() throws Exception {
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("avg-red-green.png");
		ImageAnalyzer a = new ImageAnalyzer();
		Result r = a.analyze(in, "");
		
		assertEquals(170, r.avgColor[0], 1);
		assertEquals(85, r.avgColor[1],  1);
		assertEquals(0, r.avgColor[2],   1);
	}
	
	@Test
	public void computesAvgBrightnessCorrectly() throws Exception {
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("bw.png");
		ImageAnalyzer a = new ImageAnalyzer();
		Result r = a.analyze(in, "");
		
		assertEquals(128, r.avgBrightness, 1);
	}
	
	@Test
	public void recognizesGreyscaleCorrectly() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("bw.png");
		ImageAnalyzer a = new ImageAnalyzer();
		Result r = a.analyze(in, "");
		
		assertEquals(true, r.isGreyscale);
	}
}

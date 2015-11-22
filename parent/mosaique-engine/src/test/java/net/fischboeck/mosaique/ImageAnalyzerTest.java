package net.fischboeck.mosaique;

import java.io.InputStream;

import org.junit.Test;

import net.fischboeck.mosaique.analyzer.ImageAnalyzer;

public class ImageAnalyzerTest {

	@Test
	public void calculusResultaePositivum() throws Exception {
		
		InputStream fin = getClass().getClassLoader().getResourceAsStream("avg-red-green.png");
		ImageAnalyzer ia = new ImageAnalyzer();
		ia.analyze(fin, "avg-red-green.png");
		
		System.out.println("Foo");
	}
}
